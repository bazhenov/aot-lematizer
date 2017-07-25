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
	private final MmapIntList postfixPl;

	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r");
				 FileChannel channel = f.getChannel()) {

			MappedByteBuffer prefixPostingList = mapBlock(f, channel);
			MappedByteBuffer postfixPostingList = mapBlock(f, channel);
			MappedByteBuffer prefixBlock = mapBlock(f, channel);
			MappedByteBuffer postfixBlock = mapBlock(f, channel);

			prefixPl = new MmapIntList(prefixPostingList);
			postfixPl = new MmapIntList(postfixPostingList);
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
				MmapIntList.IntIterator postfixPlIterator = lookupPostfixTree(word, i);
				if (postfixPlIterator != null) {
					MmapIntList.IntIterator prefixPlIterator = prefixPl.iterator(prefixPlAddress);

					System.out.printf("Combination %s-%s\n", word.substring(0, i), word.substring(i, word.length()));

					/*while (prefixPlIterator.hasNext()) {
						System.out.println(prefixPlIterator.next());
					}

					System.out.println("----");
					while (postfixPlIterator.hasNext()) {
						System.out.println(postfixPlIterator.next());
					}*/

					int wordBaseIdx;
					while ((wordBaseIdx = postfixPlIterator.nextCommon(prefixPlIterator)) != 0) {
						found = true;
						System.out.printf("Allowed combination: %s-%s [%d]\n", word.substring(0, i), word.substring(i, word.length()), wordBaseIdx);
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

	private MmapIntList.IntIterator lookupPostfixTree(String word, int s) {
		MmapTrie.State state = postfixTrie.init();
		for (int i = s; i < word.length(); i++) {
			byte c = safeCastCharacter(word.charAt(i));
			if (!state.step(c))
				return null;
		}
		int address = state.value();
		return address > 0
			? postfixPl.iterator(address)
			: null;
	}
}
