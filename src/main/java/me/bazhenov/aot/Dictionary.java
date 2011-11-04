package me.bazhenov.aot;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;

public class Dictionary {

	private static final int BLOCK_SIZE = 16;
	private final InputStream mrdInputStream;
	private final InputStream tabInputStream;
	private List<List<Flexion>> allFlexions;
	private List<Lemma> lemmas;
	private TernarySearchTree norms;
	private Map<String, GramInfo> grammInfo = new HashMap<String, GramInfo>();

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

		AtomicInteger sequence = new AtomicInteger(1);
		List<Variation> allTheWords = newArrayList();

		for (Lemma l : lemmas) {
			List<Variation> lemmaVariations = buildAllVariations(l, allFlexions.get(l.getFlexionIndex()), sequence);
			Variation lemmaVariation = lemmaVariations.remove(0);
			allTheWords.add(lemmaVariation);

			for (Variation v : lemmaVariations) {
				v.setLemmaIndex(lemmaVariation.getId());
				allTheWords.add(v);
			}
		}
		Collections.sort(allTheWords, new VariationComparator());

		List<Variation> words = new ArrayList<Variation>(BLOCK_SIZE);
		List<Block> blocks = newArrayList();
		for (Variation v : allTheWords) {
			if (words.size() > BLOCK_SIZE) {
				blocks.add(new Block(words));
				words = new ArrayList<Variation>(BLOCK_SIZE);
			}
			words.add(v);
		}
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
		List<GramInfo> info = new ArrayList<GramInfo>();
		/*for (Variation v : norms.get(word.toLowerCase())) {
			info.add(grammInfo.get(v.getAncode()));
		}*/
		return info;
	}

	public Set<Variation> getWordNorm(String word) {
		Set<Variation> wordNorms = new HashSet<Variation>();
		/*for (Variation v : norms.get(word.toLowerCase())) {
			if (v.isLemma()) {
				wordNorms.add(v);
			} else {
				wordNorms.add(variations.get(v.getLemmaIndex()));
			}
		}*/
		return wordNorms;
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
