package me.bazhenov.aot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;

public class MmapDictionary {

	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;
	private final MmapIntList prefixPl;

	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r");
				 FileChannel channel = f.getChannel()) {

			MappedByteBuffer prefixPostingList = mapBlock(f, channel);
			MappedByteBuffer prefixBlock = mapBlock(f, channel);
			MappedByteBuffer postfixBlock = mapBlock(f, channel);

			prefixPl = new MmapIntList(prefixPostingList);
			prefixTrie = new MmapTrie(prefixBlock);
			postfixTrie = new MmapTrie(postfixBlock);
		}
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

	public boolean checkExists(String word) {
		boolean found = false;
		MmapTrie.State state = prefixTrie.init();
		for (int i = 0; i < word.length(); i++) {
			int prefixPlAddress = state.value();
			if (prefixPlAddress != 0) {
				if (lookupPostfixTree(word, i)) {
					found = true;
					System.out.printf("Allowed combination: %s-%s\n", word.substring(0, i), word.substring(i, word.length()));

					MmapIntList.IntIterator prefixPlIterator = prefixPl.iterator(prefixPlAddress);
					while (prefixPlIterator.hasNext()) {
						System.out.println(prefixPlIterator.next());
					}
				}
			}

			byte c = safeCastCharacter(word.charAt(i));
			if (!state.step(c)) {
				break;
			}
		}
		return found;
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
