package me.bazhenov.aot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static me.bazhenov.aot.Utils.dictionaryCharset;
import static me.bazhenov.aot.Utils.safeCastCharacter;

public class MmapDictionary {

	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;
	private final MmapIntList prefixPl;
	private final MmapIntList postfixPl;
	private final MmapFixedWidthIntBlock wordBaseToFlexion;
	private final MmapFlexionList flexions;

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
			for (int i = 0; i < word.length(); i++) {
				int prefixPlAddress = state.value();
				if (prefixPlAddress != 0) {
					MmapIntList.IntIterator postfixPlIterator = lookupPostfixTree(word, i);
					if (postfixPlIterator != null) {
						MmapIntList.IntIterator prefixPlIterator = prefixPl.iterator(prefixPlAddress);

						int wordBaseIdx;
						while ((wordBaseIdx = postfixPlIterator.nextCommon(prefixPlIterator)) != 0) {
							callback.foundWord(wordBaseIdx, word.length() - i);
						}
					}
				}

				byte c = safeCastCharacter(word.charAt(i));
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
			byte[] bytes = flexions.retrievedNormPostfix(flexionId);
			String base = word.substring(0, word.length() - endingLength);
			if (bytes.length == 0) {
				norms.add(base);
			} else {
				norms.add(base + new String(bytes, dictionaryCharset));
			}
		});
		return norms;
	}

	private MmapIntList.IntIterator lookupPostfixTree(String word, int start) {
		MmapTrie.State state = postfixTrie.init();
		for (int i = start; i < word.length(); i++)
			if (!state.step(safeCastCharacter(word.charAt(i))))
				return null;

		if (!state.step(safeCastCharacter(word.charAt(0))))
			return null;

		int address = state.value();
		return address > 0
			? postfixPl.iterator(address)
			: null;
	}

	interface FoundWordsConsumer {

		void foundWord(int wordIdx, int endingLength);
	}
}
