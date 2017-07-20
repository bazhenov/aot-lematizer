package me.bazhenov.aot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;

public class MmapDictionary {

	private final MappedByteBuffer prefixBlock;
	private final MappedByteBuffer postfixBlock;
	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;

	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r");
				 FileChannel channel = f.getChannel()) {

			prefixBlock = mapBlock(f, channel);
			postfixBlock = mapBlock(f, channel);
		}

		prefixTrie = new MmapTrie(prefixBlock);
		postfixTrie = new MmapTrie(postfixBlock);
	}

	private MappedByteBuffer mapBlock(RandomAccessFile f, FileChannel channel) throws IOException {
		int checksum = f.readInt();
		if (checksum != 0xDEADC0DE) {
			throw new IllegalStateException("Incorrect block checksum at offset: " + f.getFilePointer());
		}
		int length = f.readInt();
		long start = f.getFilePointer();
		f.seek(f.getFilePointer() + length);
		return channel.map(READ_ONLY, start, length);
	}

	public void checkExists(String word) {
		MmapTrie.State state = prefixTrie.init();
		for (int i = 0; i < word.length(); i++) {
			if (state.value() != 0) {
				if (lookupPostfixTree(word, i)) {
					System.out.printf("Allowed combination: %s-%s\n", word.substring(0, i), word.substring(i, word.length()));
				}
			}
			byte c = safeCastCharacter(word.charAt(i));
			state.step(c);
		}
	}

	private boolean lookupPostfixTree(String word, int s) {
		MmapTrie.State state = postfixTrie.init();
		for (int i = s; i < word.length(); i++) {
			byte c = safeCastCharacter(word.charAt(i));
			if (!state.step(c))
				return false;
		}
		return state.value() != 0;
	}
}
