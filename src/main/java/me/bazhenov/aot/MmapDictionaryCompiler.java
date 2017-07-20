package me.bazhenov.aot;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static java.nio.channels.Channels.newChannel;
import static java.nio.charset.StandardCharsets.UTF_8;
import static me.bazhenov.aot.TernaryTreeDictionary.readSection;

public class MmapDictionaryCompiler {

	public static void compile(File dest) throws IOException {
		State state = prepareDictionaryState();
		writeState(dest, state);
	}

	private static void writeState(File dest, State state) throws IOException {
		TrieWriter writer = new TrieWriter();
		try (WritableByteChannel channel = newChannel(new FileOutputStream(dest))) {
			ByteBuffer buffer;

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

			// flexions
			readSection(reader, (line) -> {
				for (String flexion : line.split("%")) {
					if (flexion.isEmpty())
						continue;
					String[] parts = flexion.split("\\*");
					String affix = parts[0].toLowerCase().replace("ё", "е");
					state.postfixTrie.replace(affix, 1);
				}
				return null;
			});

			readSection(reader, null); // accentual models
			readSection(reader, null); // user sessions
			readSection(reader, null); // prefix sets

			readSection(reader, (line) -> {
				String[] parts = line.split(" ");
				String word = parts[0].toLowerCase();
				state.prefixTrie.replace(word, 1);
				return null;
			});
		}
		return state;
	}

	private static class State {

		Trie<Integer> prefixTrie = new Trie<>();
		Trie<Integer> postfixTrie = new Trie<>();
	}

}
