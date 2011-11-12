package me.bazhenov.aot;

import com.google.common.primitives.Ints;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;

public class Dictionary {

	private static final int BLOCK_SIZE = 8;
	private final InputStream mrdInputStream;
	private final InputStream tabInputStream;
	private List<List<Flexion>> allFlexions;
	private List<Lemma> lemmas;
	private TernarySearchTree norms;
	private Map<String, GramInfo> grammInfo = new HashMap<String, GramInfo>();
	private int[] idIndex;
	private List<Block> blocks;

	public Dictionary(InputStream mrdInputStream, InputStream tabInputStream) throws IOException {
		this.mrdInputStream = mrdInputStream;
		this.tabInputStream = tabInputStream;
		loadDictionary(mrdInputStream, tabInputStream);
	}

	private void loadDictionary(InputStream mrdInputStream, InputStream tabInputStream) throws IOException {
		BufferedInputStream is = new BufferedInputStream(mrdInputStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		allFlexions = readSection(reader, new FlexionMapper());
		readSection(reader, new NullMapper()); // accentual models
		readSection(reader, new NullMapper()); // user sessions
		readSection(reader, new NullMapper()); // prefix sets
		lemmas = readSection(reader, new LemmaMapper());
		reader.close();
		is.close();

		is = new BufferedInputStream(tabInputStream);
		reader = new BufferedReader(new InputStreamReader(is));
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
		reader.close();
		is.close();
		Writer os = new PrintWriter(new FileOutputStream("dict.result"));

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
		blocks = newArrayList();
		OutputStream result = new BufferedOutputStream(new FileOutputStream("result"));
		idIndex = new int[allTheWords.size() + 1];

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

		for (int blockOffset : idIndex) {
			result.write(Ints.toByteArray(blockOffset));
		}
		result.close();
	}

	private static <O> List<O> readSection(BufferedReader reader, Mapper<String, O> mapper) throws IOException {
		int sectionLength = parseInt(reader.readLine());

		List<O> output = new ArrayList<O>(sectionLength);
		for (int i = 0; i < sectionLength; i++) {
			output.add(mapper.map(reader.readLine()));
		}
		return output;
	}

	public List<GramInfo> getGramInfo(String word) {
		for (int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(0);
			if (b.getFirstWord().compareToIgnoreCase(word) > 0) {
				continue;
			}
			return null;//findVariation(blocks.get(i-1), word);
		}
		return newArrayList();
	}

	private List<Variation> findVariations(Block block, String word) {
		List<Variation> info = new ArrayList<Variation>();
		for (int i = 0; i < block.size(); i++) {
			Variation variation = block.getVariationAtOffset(i);
			if (variation.getWord().equalsIgnoreCase(word)) {
				int lemmaIndex = variation.getLemmaIndex();
				info.add(findVariation(blocks.get(idIndex[lemmaIndex]), lemmaIndex));
			}
		}
		return info;
	}

	private Variation findVariation(Block block, int id) {
		for (int i = 0; i < block.size(); i++) {
			Variation variation = block.getVariationAtOffset(i);
			if (variation.getId() == id) {
				return variation;
			}
		}
		throw new RuntimeException("Ooops");
	}

	public Set<Variation> getWordNorm(String word) {
		Block block = findPossibleBlockByWord(word);
		return block != null ? newHashSet(findVariations(block, word)) : null;
	}

	private Block findPossibleBlockByWord(String word) {
		for (int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			int comparsionResult = b.getFirstWord().compareToIgnoreCase(word);
			if (comparsionResult == 0) {
				return b;
			}else if (comparsionResult < 0) {
				continue;
			}
			return (i > 0) ? blocks.get(i - 1) : null;
		}
		return blocks.get(blocks.size() - 1);
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
