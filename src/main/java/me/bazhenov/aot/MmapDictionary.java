package me.bazhenov.aot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.ThreadLocal.withInitial;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static me.bazhenov.aot.Utils.safeByteToChar;
import static me.bazhenov.aot.Utils.safeCharToByte;

public class MmapDictionary {

	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;
	private final MmapIntList prefixPl;
	private final MmapIntList postfixPl;
	private final MmapFixedWidthIntBlock wordBaseToFlexion;
	private final MmapFlexionList flexions;

	private final ThreadLocal<byte[]> characters = withInitial(() -> new byte[0]);

	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r");
				 FileChannel channel = f.getChannel()) {

			ByteBuffer flexionBlock = mapBlock(f, channel);
			ByteBuffer wordBaseToFlexionBlock = mapBlock(f, channel);
			ByteBuffer prefixPostingList = mapBlock(f, channel);
			ByteBuffer postfixPostingList = mapBlock(f, channel);
			ByteBuffer prefixBlock = mapBlock(f, channel);
			ByteBuffer postfixBlock = mapBlock(f, channel);

			prefixPl = new MmapIntList(prefixPostingList);
			postfixPl = new MmapIntList(postfixPostingList);
			prefixTrie = new MmapTrie(prefixBlock);
			postfixTrie = new MmapTrie(postfixBlock);
			wordBaseToFlexion = new MmapFixedWidthIntBlock(wordBaseToFlexionBlock);
			flexions = new MmapFlexionList(flexionBlock);
		}
	}

	private ByteBuffer mapBlock(RandomAccessFile f, FileChannel channel) throws IOException {
		int header = f.readInt();
		if (header != 0xDEADC0DE) {
			throw new IllegalStateException("Incorrect block header at offset: " + (f.getFilePointer() - 4));
		}
		int length = f.readInt();
		long start = f.getFilePointer();

		// по контракту этот метод сдвигает указатель файла на конец блока, который отображается в память
		f.seek(start + length);

		return channel.map(READ_ONLY, start, length);
	}

	public int countWords(String word) {
		AtomicInteger found = new AtomicInteger(0);
		doFind(word, (wordId, l) -> found.incrementAndGet());
		return found.get();
	}

	private void doFind(String word, FoundWordsConsumer callback) {
		try {
			MmapTrie.State state = prefixTrie.init();

			int length = word.length();
			byte[] characters = new byte[length];

			for (int i = 0; i < length; i++)
				characters[i] = safeCharToByte(word.charAt(i));

			MmapIntList.IntIterator prefixPlIterator = prefixPl.iterator();
			MmapIntList.IntIterator postfixPlIterator = postfixPl.iterator();

			for (int i = 0; i < length; i++) {
				int prefixPlAddress = state.value();
				if (prefixPlAddress != 0) {
					if (lookupPostfixTree(postfixPlIterator, characters, i, length)) {
						prefixPlIterator.reset(prefixPlAddress);

						int wordBaseIdx;
						while ((wordBaseIdx = postfixPlIterator.nextCommon(prefixPlIterator)) != 0) {
							callback.foundWord(wordBaseIdx, word.length() - i);
						}
					}
				}

				byte c = characters[i];
				if (!state.step(c)) {
					break;
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Unable to lookup word: " + word, e);
		}
	}

	public List<String> getWordNorms(String word) {
		List<String> norms = new ArrayList<>();
		doFind(word, (wordId, endingLength) -> {
			int flexionId = wordBaseToFlexion.getValue(wordId - 1);
			byte[] ending = flexions.retrievedNormPostfix(flexionId);
			int baseLength = word.length() - endingLength;

			if (ending.length == 0) {
				norms.add(word.substring(0, baseLength));
			} else {
				norms.add(makeWord(word, baseLength, ending));
			}
		});
		return norms;
	}

	private String makeWord(String base, int baseLength, byte[] ending) {
		char[] result = new char[baseLength + ending.length];
		for (int i = 0; i < baseLength; i++)
			result[i] = base.charAt(i);
		for (int i = 0; i < ending.length; i++)
			result[i + baseLength] = safeByteToChar(ending[i]);

		return new String(result);
	}

	private boolean lookupPostfixTree(MmapIntList.IntIterator postfixPlIterator, byte[] word, int start, int length) {
		MmapTrie.State state = postfixTrie.init();
		for (int i = length - 1; i >= start; i--)
			if (!state.step(word[i]))
				return false;

		if (!state.step(word[0]))
			return false;

		int address = state.value();
		if (address > 0) {
			postfixPlIterator.reset(address);
			return true;
		} else {
			return false;
		}
	}

	interface FoundWordsConsumer {

		void foundWord(int wordIdx, int endingLength);
	}
}
