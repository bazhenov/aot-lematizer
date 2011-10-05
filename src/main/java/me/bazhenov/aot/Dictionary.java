package me.bazhenov.aot;

import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Dictionary {

	private final InputStream mrdInputStream;
	private final InputStream tabInputStream;
	private List<List<Flexion>> allFlexions;
	private List<Lemma> lemmas;
	private Map<String, Set<Variation>> norms;
	private List<Variation> variations = new ArrayList<Variation>();
	private Map<String, GramInfo> grammInfo = new HashMap<String, GramInfo>();

	public Dictionary(InputStream mrdInputStream, InputStream tabInputStream) throws IOException {
		this.mrdInputStream = mrdInputStream;
		this.tabInputStream = tabInputStream;
		loadDictionary(mrdInputStream, tabInputStream);
	}

	public static TernarySearchTree buildTrie(InputStream mrd, InputStream tab, OutputStream out)
		throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(mrd));
		List<List<Flexion>> allFlexions = readSection(reader, new FlexionMapper());
		readSection(reader, new NullMapper()); // accentual models
		readSection(reader, new NullMapper()); // user sessions
		readSection(reader, new NullMapper()); // prefix sets
		List<Lemma> lemmas = readSection(reader, new LemmaMapper());
		TernarySearchTree tree = new TernarySearchTree();
		List<String> allVariations = new ArrayList<String>();
		for (Lemma l : lemmas) {
			List<Variation> variations = buildAllVariations(l, allFlexions.get(l.getFlexionIndex()));
			/*Variation v = new Variation(variations.remove(0), 0);
			allVariations.addAll(variations);*/
		}

		for (int i = allVariations.size() - 1; i >= 0; i--) {
			tree.insert(allVariations.get(i), 1);
		}

		DataOutputStream os = new DataOutputStream(out);
		tree.writeTo(os);
		return tree;
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
		while((line = reader.readLine()) != null) {
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


		norms = new TreeMap<String, Set<Variation>>();
		for (Lemma l : lemmas) {
			List<Variation> variations = buildAllVariations(l, allFlexions.get(l.getFlexionIndex()));
			Variation lemmaVariation = variations.remove(0);

			this.variations.add(lemmaVariation);
			int lemmaIndex = this.variations.size() - 1;

			for (Variation v : variations) {
				v.setLemmaIndex(lemmaIndex);
				this.variations.add(v);
				if (norms.containsKey(v.getWord())) {
					norms.get(v.getWord()).add(v);
				} else {
					Set<Variation> wordLemmas = new HashSet<Variation>();
					wordLemmas.add(v);
					norms.put(v.getWord(), wordLemmas);
				}
			}
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
		for (Variation v : norms.get(word.toLowerCase())) {
			info.add(grammInfo.get(v.getAncode()));
		}
		return info;
	}

	public Set<Variation> getWordNorm(String word) {
		Set<Variation> wordNorms = new HashSet<Variation>();
		for (Variation v : norms.get(word.toLowerCase())) {
			if (v.isLemma()) {
				wordNorms.add(v);
			} else {
				wordNorms.add(variations.get(v.getLemmaIndex()));
			}
		}
		return wordNorms;
	}

	public List<Lemma> getLemmas() {
		return lemmas;
	}

	public static List<Variation> buildAllVariations(Lemma lemma, List<Flexion> flexions) {
		List<Variation> variations = new LinkedList<Variation>();
		if (lemma.getLemma().equals("#")) {
			for (Flexion flexion : flexions) {
				variations.add(new Variation(flexion.getEnding(), flexion.getAncode()));
			}
		} else {
			for (Flexion flexion : flexions) {
				variations.add(new Variation(lemma.getLemma() + flexion.getEnding(), flexion.getAncode()));
			}
		}
		return variations;
	}
}
