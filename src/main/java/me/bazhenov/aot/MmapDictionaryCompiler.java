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

			ByteBuffer b = ByteBuffer.allocate(100 * 1024 * 1024);
			// чтобы ни один адрес не был нулевым
			b.put((byte) 0xFF);
			for (Addressed<Set<Integer>> a : state.prefixPostingLists) {
				a.setAddress(b.position());
				writeToByteBuffer(new ArrayList<>(a.getRef()), b);
			}
			b.flip();
			writeBuffer(channel, b);

			writeBuffer(channel, writer.write(state.prefixTrie));
			writeBuffer(channel, writer.write(state.postfixTrie));
		}
	}

	private static void writeBuffer(WritableByteChannel channel, ByteBuffer buffer) throws IOException {
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
			readSection(reader, (line) -> {
				for (String flexion : line.split("%")) {
					if (flexion.isEmpty())
						continue;
					String[] parts = flexion.split("\\*");
					String affix = parts[0].toLowerCase().replace("ё", "е");
					Addressed<Set<Integer>> wrapper = new Addressed<>(null);
					wrapper.setAddress(1);
					state.postfixTrie.replace(affix, wrapper);
				}
				return null;
			});

			readSection(reader, null); // accentual models
			readSection(reader, null); // user sessions
			readSection(reader, null); // prefix sets

			AtomicInteger wordBases = new AtomicInteger(1);


			readSection(reader, (line) -> {
				int wordIdx = wordBases.getAndIncrement();
				String[] parts = line.split(" ");
				String word = parts[0].toLowerCase();
				int flexionIdx = parseInt(parts[1]);

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
		}
		return state;
	}

	private static class State {

		Trie<Addressed<Set<Integer>>> prefixTrie = new Trie<>();
		Trie<Addressed<Set<Integer>>> postfixTrie = new Trie<>();
		List<Addressed<Set<Integer>>> prefixPostingLists = new ArrayList<>();
	}

}
