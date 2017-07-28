package me.bazhenov.aot;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.Channels.newChannel;
import static java.nio.charset.StandardCharsets.UTF_8;
import static me.bazhenov.aot.TernaryTreeDictionary.readSection;
import static me.bazhenov.aot.Utils.writeAndGetBeforePosition;

public class MmapDictionaryCompiler {

	public static void compileInto(File dest) throws IOException {
		State state = prepareDictionaryState();
		writeState(dest, state);
	}

	private static void writeState(File dest, State state) throws IOException {
		TrieWriter writer = new TrieWriter();
		try (WritableByteChannel channel = newChannel(new FileOutputStream(dest))) {

			// Выделяем 100 мегабайт чтоб наверняка хватило. Эффективность процесса компиляции словаря не суть важна.
			ByteBuffer b = allocate(100 * 1024 * 1024);

			int[] flexionIndexToOffset = new int[state.flexions.size()];

			pushFlexions(b, state.flexions, flexionIndexToOffset);
			writeBlock(channel, b);

			pushWordBaseToFlexionIndex(b, state.wordBaseToFlexionIndex, flexionIndexToOffset);
			writeBlock(channel, b);

			pushPostingList(b, state.prefixPostingLists);
			writeBlock(channel, b);

			pushPostingList(b, state.postfixPostingLists);
			writeBlock(channel, b);

			writeBlock(channel, writer.write(state.prefixTrie));
			writeBlock(channel, writer.write(state.postfixTrie));
		}
	}

	private static void pushFlexions(ByteBuffer b, List<List<Flexion>> flexions, int[] indexToOffset) {
		b.clear();

		for (int i = 0; i < flexions.size(); i++) {
			int offset = writeAndGetBeforePosition(b, MmapFlexionList.writeToByteBuffer(flexions.get(i)));
			indexToOffset[i] = offset;
		}

		b.flip();
	}

	private static void pushWordBaseToFlexionIndex(ByteBuffer b, List<Integer> indexes, int[] indexToOffset) {
		b.clear();

		for (int idx : indexes) {
			writeAndGetBeforePosition(b, MmapFixedWidthIntBlock.writeToByteBuffer(indexToOffset[idx]));
		}

		b.flip();
	}

	private static void pushPostingList(ByteBuffer b, List<Addressed<SortedSet<Integer>>> postingLists) {
		b.clear();
		// чтобы ни один адрес в MmapTrie не был нулевым, начинаем запись PL со смещением в один байт
		b.put((byte) 0xFF);

		for (Addressed<SortedSet<Integer>> lst : postingLists) {
			int position = writeAndGetBeforePosition(b, MmapIntList.writeToByteBuffer(lst.getRef()));
			lst.setAddress(position);
		}

		b.flip();
	}

	/**
	 * Записывает блок в канал
	 *
	 * @param channel канал
	 * @param buffer  буффер содержащий блок. Должен быть готов к записи (см. {@link ByteBuffer#flip()})
	 */
	private static void writeBlock(WritableByteChannel channel, ByteBuffer buffer) throws IOException {
		// Заголовок каждого блока состоит из маркера 0xDE 0xAD 0xC0 0xDE и последующего размера блока в байтах
		ByteBuffer header = allocate(8);
		header.putInt(0xDEADC0DE);
		header.putInt(buffer.limit());
		header.flip();

		writeFully(header, channel);
		writeFully(buffer, channel);
	}

	private static void writeFully(ByteBuffer buffer, WritableByteChannel channel) throws IOException {
		while (buffer.hasRemaining())
			channel.write(buffer);
	}

	private static State prepareDictionaryState() throws IOException {
		State state = new State();

		try (InputStream is = MmapDictionaryCompiler.class.getResourceAsStream("/mrd_old");
				 BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {

			List<String> flexions = new ArrayList<>();
			// flexions
			readSection(reader, flexions::add);

			for (String line : flexions) {
				List<Flexion> flexia = new ArrayList<>();
				for (String flex : line.split("%")) {
					if (flex.isEmpty())
						continue;
					String[] parts = flex.split("\\*");
					String postfix = prepare(parts[0]);
					String ancode = parts[1].toLowerCase();
					flexia.add(new Flexion(ancode, postfix));
				}
				state.flexions.add(flexia);
			}

			readSection(reader, null); // accentual models
			readSection(reader, null); // user sessions
			readSection(reader, null); // prefix sets

			AtomicInteger wordBases = new AtomicInteger(1);

			readSection(reader, (line) -> {
				int wordIdx = wordBases.getAndIncrement();
				String[] parts = line.split(" ");
				String base = prepare(parts[0]);
				if (base.equals("#")) // слово с пустой неизменяемой основной
					base = "";
				int flexionIdx = parseInt(parts[1]);

				// индексируем постфиксы слова
				for (Flexion f : state.flexions.get(flexionIdx)) {
					String ending = f.getEnding();
					String fullWord = base + ending;
					String length = Integer.toString(fullWord.length() % 10);
					String postfix = new StringBuilder(ending).reverse().toString() + fullWord.charAt(0) + length;

					Addressed<SortedSet<Integer>> existingPl = state.postfixTrie.search(postfix);
					if (existingPl == null) {
						existingPl = new Addressed<>(new TreeSet<>());
						state.postfixPostingLists.add(existingPl);
						state.postfixTrie.add(postfix, existingPl);
					}
					existingPl.getRef().add(wordIdx);
				}

				state.wordBaseToFlexionIndex.add(flexionIdx);

				Addressed<SortedSet<Integer>> existedPostingList = state.prefixTrie.search(base);
				if (existedPostingList != null) {
					existedPostingList.getRef().add(wordIdx);
				} else {
					SortedSet<Integer> newPostingList = new TreeSet<>();
					newPostingList.add(wordIdx);
					Addressed<SortedSet<Integer>> wrapper = new Addressed<>(newPostingList);
					state.prefixPostingLists.add(wrapper);
					state.prefixTrie.add(base, wrapper);
				}
				return null;
			});
		}
		return state;
	}

	private static String prepare(String word) {
		return word.toLowerCase().replaceAll("ё", "е");
	}

	private static class State {

		Trie<Addressed<SortedSet<Integer>>> prefixTrie = new Trie<>();
		Trie<Addressed<SortedSet<Integer>>> postfixTrie = new Trie<>();
		List<Addressed<SortedSet<Integer>>> prefixPostingLists = new ArrayList<>();
		List<Addressed<SortedSet<Integer>>> postfixPostingLists = new ArrayList<>();
		List<List<Flexion>> flexions = new ArrayList<>();
		List<Integer> wordBaseToFlexionIndex = new ArrayList<>();
	}

}
