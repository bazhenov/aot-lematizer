package me.bazhenov.aot;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static me.bazhenov.aot.Utils.*;

public class MmapDictionary {

	private final MmapTrie prefixTrie;
	private final MmapTrie postfixTrie;
	private final MmapIntList prefixPl;
	private final MmapIntList postfixPl;
	private final MmapFixedWidthIntBlock wordBaseToFlexion;
	private final MmapFlexionList flexions;

	public MmapDictionary() throws IOException {
		this(MmapDictionary.class.getResourceAsStream("/aot.dict"));
	}


	public MmapDictionary(File dictFile) throws IOException {
		try (RandomAccessFile f = new RandomAccessFile(dictFile, "r");
				 FileChannel channel = f.getChannel()) {

			flexions = new MmapFlexionList(mapBlock(f, channel));
			wordBaseToFlexion = new MmapFixedWidthIntBlock(mapBlock(f, channel));
			prefixPl = new MmapIntList(mapBlock(f, channel));
			postfixPl = new MmapIntList(mapBlock(f, channel));
			prefixTrie = new MmapTrie(mapBlock(f, channel));
			postfixTrie = new MmapTrie(mapBlock(f, channel));
		}
	}

	/**
	 * Создает словарь из {@link InputStream}'а. Данный метод выделяет память под словарь в heap-памяти.
	 *
	 * @param inputStream поток с данными словаря спозиционированный в начале файла
	 */
	public MmapDictionary(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new NullPointerException("No input stream provided");
		}
		flexions = new MmapFlexionList(readBlock(inputStream));
		wordBaseToFlexion = new MmapFixedWidthIntBlock(readBlock(inputStream));
		prefixPl = new MmapIntList(readBlock(inputStream));
		postfixPl = new MmapIntList(readBlock(inputStream));
		prefixTrie = new MmapTrie(readBlock(inputStream));
		postfixTrie = new MmapTrie(readBlock(inputStream));
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

	private static ByteBuffer readBlock(InputStream in) throws IOException {
		DataInputStream input = new DataInputStream(in);
		int header = input.readInt();
		if (header != 0xDEADC0DE) {
			throw new IllegalStateException("Incorrect block header");
		}
		int length = input.readInt();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		input.readFully(buffer.array());
		return buffer;
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
				characters[i] = charToByte(word.charAt(i));

			MmapIntList.IntIterator prefixPlIterator = prefixPl.iterator();
			MmapIntList.IntIterator postfixPlIterator = postfixPl.iterator();

			for (int i = 0; i < length; i++) {
				int prefixPlAddress = state.value();
				if (prefixPlAddress != 0) {
					if (lookupPostfixTree(postfixPlIterator, characters, i, length)) {
						prefixPlIterator.reset(prefixPlAddress);
						feedWordsInCallback(callback, prefixPlIterator, postfixPlIterator, word.length() - i);
					}
				}

				byte c = characters[i];
				if (!state.step(c)) {
					return;
				}
			}
			// проверяем слово состоящее полностью из префикса
			int prefixPlAddress = state.value();
			if (prefixPlAddress != 0) {
				if (lookupPostfixTree(postfixPlIterator, characters, length, length)) {
					prefixPlIterator.reset(prefixPlAddress);
					feedWordsInCallback(callback, prefixPlIterator, postfixPlIterator, 0);
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Unable to lookup word: " + word, e);
		}
	}

	private void feedWordsInCallback(FoundWordsConsumer callback, MmapIntList.IntIterator prefix,
																	 MmapIntList.IntIterator postfix, int endingLength) {
		int wordBaseIdx;
		while ((wordBaseIdx = postfix.nextCommon(prefix)) != 0) {
			callback.foundWord(wordBaseIdx, endingLength);
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

		char c;
		switch (length % 10) {
			case 0:
				c = '0';
				break;
			case 1:
				c = '1';
				break;
			case 2:
				c = '2';
				break;
			case 3:
				c = '3';
				break;
			case 4:
				c = '4';
				break;
			case 5:
				c = '5';
				break;
			case 6:
				c = '6';
				break;
			case 7:
				c = '7';
				break;
			case 8:
				c = '8';
				break;
			case 9:
				c = '9';
				break;
			default:
				throw new IllegalStateException("Ooops");
		}

		if (!state.step(safeCharToByte(c)))
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
