package me.bazhenov.aot;

import com.google.common.primitives.Ints;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.ByteStreams.readFully;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.Integer.parseInt;

public class Dictionary {

	private static final int BLOCK_SIZE = 16;
	private Map<String, GramInfo> grammInfo = new HashMap<String, GramInfo>();
	private int[] idIndex;
	private List<Block> blocks;

	public Dictionary(InputStream dictionary, InputStream tabInputStream) throws IOException {
		grammInfo = buildGramInfo(tabInputStream);
		int length = readInt(dictionary);
		blocks = newArrayListWithCapacity(length);
		while (length-- > 0) {
			blocks.add(Block.readFrom(dictionary));
		}

		length = readInt(dictionary);
		idIndex = new int[length];
		for (int i = 0; i < length; i++) {
			idIndex[i] = readInt(dictionary);
		}
	}

	public static int readInt(InputStream dictionary) throws IOException {
		byte length[] = new byte[4];
		readFully(dictionary, length);
		return Ints.fromByteArray(length);
	}

	public static void writeInt(OutputStream out, int length) throws IOException {
		out.write(Ints.toByteArray(length));
	}

	public static void compileDictionary(InputStream mrdInputStream, OutputStream out)
		throws IOException {
		BufferedInputStream is = new BufferedInputStream(mrdInputStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<List<Flexion>> allFlexions = readSection(reader, new FlexionMapper());
		readSection(reader, new NullMapper()); // accentual models
		readSection(reader, new NullMapper()); // user sessions
		readSection(reader, new NullMapper()); // prefix sets
		List<Lemma> lemmas = readSection(reader, new LemmaMapper());
		reader.close();
		is.close();


		AtomicInteger sequence = new AtomicInteger(1);
		List<Variation> allTheWords = newArrayList();
		for (Lemma l : lemmas) {
			List<Variation> lemmaVariations = buildAllVariations(l, allFlexions.get(l.getFlexionIndex()), sequence);
			Variation lemmaVariation = lemmaVariations.get(0);

			for (Variation v : lemmaVariations) {
				v.setLemmaIndex(lemmaVariation.getId());
				allTheWords.add(v);
			}
		}
		Collections.sort(allTheWords, new VariationComparator());

		List<Variation> words = new ArrayList<Variation>(BLOCK_SIZE);
		List<Block> blocks = newArrayList();
		int[] idIndex = new int[allTheWords.size() + 1];

		String previosWord = null;
		for (Variation v : allTheWords) {
			if (words.size() >= BLOCK_SIZE && (previosWord != null && !v.getWord().equalsIgnoreCase(previosWord))) {
				Block block = new Block(words);

				blocks.add(block);
				words = new ArrayList<Variation>(BLOCK_SIZE);
			}
			idIndex[v.getId()] = blocks.size();
			words.add(v);
			previosWord = v.getWord();
		}
		Block block = new Block(words);
		blocks.add(block);

		writeInt(out, blocks.size());
		for (Block b : blocks) {
			b.writeTo(out);
		}

		writeInt(out, idIndex.length);
		for (int blockOffset : idIndex) {
			writeInt(out, blockOffset);
		}
	}

	private static Map<String, GramInfo> buildGramInfo(InputStream is) throws IOException {
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

	private static <O> List<O> readSection(BufferedReader reader, Mapper<String, O> mapper) throws IOException {
		int sectionLength = parseInt(reader.readLine());

		List<O> output = new ArrayList<O>(sectionLength);
		for (int i = 0; i < sectionLength; i++) {
			output.add(mapper.map(reader.readLine()));
		}
		return output;
	}

	public GramInfo getGramInfo(String gram) {
		return grammInfo.get(gram);
	}

	private List<Variation> findVariations(Block block, String word) {
		List<Variation> info = new ArrayList<Variation>();
		for (Variation v : block.getVariations(word)) {
			int lemmaIndex = v.getLemmaIndex();
			info.add(findVariation(blocks.get(idIndex[lemmaIndex]), lemmaIndex));
		}
		return info;
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

	public static List<Variation> buildAllVariations(Lemma lemma, List<Flexion> flexions, AtomicInteger sequence) {
		List<Variation> variations = new LinkedList<Variation>();
		if (lemma.getLemma().equals("#")) {
			for (Flexion flexion : flexions) {
				variations.add(new Variation(flexion.getEnding(), flexion.getAncode(), sequence.getAndIncrement()));
			}
		} else {
			for (Flexion flexion : flexions) {
				variations.add(new Variation(lemma.getLemma() + flexion.getEnding(), flexion.getAncode(), sequence.getAndIncrement()));
			}
		}
		return variations;
	}
}
