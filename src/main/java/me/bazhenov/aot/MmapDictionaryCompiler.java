package me.bazhenov.aot;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.nio.channels.Channels.newChannel;
import static java.nio.charset.StandardCharsets.UTF_8;
import static me.bazhenov.aot.MmapIntList.writeToByteBuffer;
import static me.bazhenov.aot.TernaryTreeDictionary.readSection;

public class MmapDictionaryCompiler {

	public static void compile(File dest) throws IOException {
		State state = prepareDictionaryState();
		writeState(dest, state);
	}

	private static void writeState(File dest, State state) throws IOException {
		TrieWriter writer = new TrieWriter();
		try (WritableByteChannel channel = newChannel(new FileOutputStream(dest))) {

			// Выделяем 100 мегабайт чтоб наверняка хватило. Эффективность процесса компиляции словаря не суть важна.
			ByteBuffer b = ByteBuffer.allocate(100 * 1024 * 1024);

			pushPostingList(b, state.prefixPostingLists);
			writeBuffer(channel, b);

			b.clear();
			pushPostingList(b, state.postfixPostingLists);
			writeBuffer(channel, b);

			writeBuffer(channel, writer.write(state.prefixTrie));
			writeBuffer(channel, writer.write(state.postfixTrie));
		}
	}

	private static void pushPostingList(ByteBuffer b, List<Addressed<Set<Integer>>> postingLists) {
		// чтобы ни один адрес в MmapTrie не был нулевым, начинаем запись PL со смещением в один байт
		b.put((byte) 0xFF);

		for (Addressed<Set<Integer>> lst : postingLists) {
			lst.setAddress(b.position());
			writeToByteBuffer(new ArrayList<>(lst.getRef()), b);
		}

		b.flip();
	}

	private static void writeBuffer(WritableByteChannel channel, ByteBuffer buffer) throws IOException {
		// Заголовок каждого блока состоит из маркера 0xDE 0xAD 0xC0 0xDE и последующего размера блока в байтах
		ByteBuffer header = ByteBuffer.allocate(8);
		header.putInt(0xDEADC0DE);
		header.putInt(buffer.limit());
		header.flip();
		channel.write(header);
		channel.write(buffer);
	}

	private static State prepareDictionaryState() throws IOException {
		State state = new State();

		try (InputStream is = MmapDictionaryCompiler.class.getResourceAsStream("/mrd_old");
				 BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {

			List<String> flexions = new ArrayList<>();
			// flexions
			readSection(reader, flexions::add);

			readSection(reader, null); // accentual models
			readSection(reader, null); // user sessions
			readSection(reader, null); // prefix sets

			AtomicInteger wordBases = new AtomicInteger(1);


			Set<Integer>[] flexionToWordBasesIndex = new Set[flexions.size()];

			readSection(reader, (line) -> {
				int wordIdx = wordBases.getAndIncrement();
				String[] parts = line.split(" ");
				String word = parts[0].toLowerCase();
				if (word.equals("#")) // слово с пустой неизменяемой основной
					word = "";
				int flexionIdx = parseInt(parts[1]);

				if (flexionToWordBasesIndex[flexionIdx] == null)
					flexionToWordBasesIndex[flexionIdx] = new TreeSet<>();

				flexionToWordBasesIndex[flexionIdx].add(wordIdx);

				Addressed<Set<Integer>> existedPostingList = state.prefixTrie.search(word);
				if (existedPostingList != null) {
					existedPostingList.getRef().add(wordIdx);
				} else {
					Set<Integer> newPostingList = new TreeSet<>();
					newPostingList.add(wordIdx);
					Addressed<Set<Integer>> wrapper = new Addressed<>(newPostingList);
					state.prefixPostingLists.add(wrapper);
					state.prefixTrie.add(word, wrapper);
				}
				return null;
			});

			for (int i = 0; i < flexions.size(); i++) {
				String line = flexions.get(i);
				for (String flex : line.split("%")) {
					if (flex.isEmpty())
						continue;
					String[] parts = flex.split("\\*");
					String affix = parts[0].toLowerCase();

					Addressed<Set<Integer>> existingPl = state.postfixTrie.search(affix);
					if (existingPl == null) {
						existingPl = new Addressed<>(new TreeSet<>());
						state.postfixPostingLists.add(existingPl);
						state.postfixTrie.add(affix, existingPl);
					}
					existingPl.getRef().addAll(flexionToWordBasesIndex[i]);
				}
			}
		}
		return state;
	}

	private static class State {

		Trie<Addressed<Set<Integer>>> prefixTrie = new Trie<>();
		Trie<Addressed<Set<Integer>>> postfixTrie = new Trie<>();
		List<Addressed<Set<Integer>>> prefixPostingLists = new ArrayList<>();
		List<Addressed<Set<Integer>>> postfixPostingLists = new ArrayList<>();
	}

}
