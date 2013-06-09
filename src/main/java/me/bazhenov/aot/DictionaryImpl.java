package me.bazhenov.aot;

import com.google.common.primitives.Ints;

import java.io.*;
import java.util.*;
import java.util.zip.ZipFile;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.ByteStreams.readFully;
import static com.google.common.io.Closeables.closeQuietly;

public class DictionaryImpl implements Iterable<Variation> {

	private Map<String, GramInfo> grammInfo = new HashMap<String, GramInfo>();
	private int[] idIndex;
	private final List<Block> blocks;

	public DictionaryImpl(File location) throws IOException {
		ZipFile dict = new ZipFile(location);
		InputStream mrd = dict.getInputStream(dict.getEntry("mrd"));
		InputStream tab = dict.getInputStream(dict.getEntry("tab"));

		grammInfo = buildGramInfo(tab);
		int length = readInt(mrd);
		blocks = newArrayListWithCapacity(length);
		while (length-- > 0) {
			blocks.add(Block.readFrom(mrd));
		}

		length = readInt(mrd);
		idIndex = new int[length];
		for (int i = 0; i < length; i++) {
			idIndex[i] = readInt(mrd);
		}
	}

	public static int readInt(InputStream dictionary) throws IOException {
		byte length[] = new byte[4];
		readFully(dictionary, length);
		return Ints.fromByteArray(length);
	}

	public static Map<String, GramInfo> buildGramInfo(InputStream is) throws IOException {
		Map<String, GramInfo> grammInfo = newHashMap();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("//") || line.isEmpty()) {
					continue;
				}
				String parts[] = line.split(" ", 2);
				String grammIndex = parts[0];
				String grammDescription = parts[1];

				grammInfo.put(grammIndex, new GramInfo(grammDescription));
			}
		} finally {
			closeQuietly(reader);
			closeQuietly(is);
		}

		return grammInfo;
	}

	public GramInfo getGramInfo(String gram) {
		return grammInfo.get(gram);
	}

	private List<Variation> findVariations(Block block, String word) {
		List<Variation> info = new ArrayList<Variation>();
		for (Variation v : block.getVariations(word)) {
			int lemmaIndex = v.getLemmaId();
			info.add(getVariation(lemmaIndex));
		}
		return info;
	}

	public Variation getVariation(int index) {
		return findVariation(blocks.get(idIndex[index]), index);
	}

	private Variation findVariation(Block block, int id) {
		return block.getVariation(id);
	}

	public List<Variation> getWordNorm(String word) {
		Block block = findPossibleBlockByWord(word);
		return block != null ? newArrayList(newHashSet(findVariations(block, word))) : null;
	}

	private Block findPossibleBlockByWord(String word) {
		int low = 0;
		int high = blocks.size() - 1;
		int mid = 0;
		int comparsionResult = 0;

		while (low <= high) {
			mid = (low + high) >>> 1;
			Block block = blocks.get(mid);

			comparsionResult = block.compareFirstWord(word);
			if (comparsionResult < 0) {
				low = mid + 1;
			} else if (comparsionResult > 0) {
				high = mid - 1;
			} else {
				return block;
			}
		}
		if (comparsionResult > 0) {
			return mid > 0
				? blocks.get(mid - 1)
				: null;
		} else {
			return blocks.get(mid);
		}
	}

	public Iterator<Variation> iterator() {
		return new DictionaryIterator();
	}

	private class DictionaryIterator implements Iterator<Variation> {

		private List<Variation> variations = newArrayList();
		private int variationIndex = 0;
		private int blockIndex = 0;

		public DictionaryIterator() {
		}

		public boolean hasNext() {
			return blockIndex < blocks.size() || variationIndex < variations.size();
		}

		public Variation next() {
			if (variationIndex >= variations.size()) {
				variations = blocks.get(blockIndex++).getAllVariations();
				variationIndex = 0;
			}
			return variations.get(variationIndex++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}