package me.bazhenov.aot;

import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Dictionary {

	private final InputStream mrdInputStream;
	private final InputStream tabInputStream;
	private List<List<Flexion>> allFlexions;
	private List<Lemma> lemmas;
	private Map<String, Set<String>> norms;

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
		norms = new TreeMap<String, Set<String>>();
		for (Lemma l : lemmas) {
			List<String> variations = buildAllVariations(l);
			String norm = variations.get(0);
			for (String word : variations) {
				Set<String> normList = norms.get(word);
				if (normList == null) {
					normList = new TreeSet<String>();
					norms.put(word, normList);
				}
				normList.add(norm);
			}
		}
	}

	private <O> List<O> readSection(BufferedReader reader, Mapper<String, O> mapper) throws IOException {
		int sectionLength = parseInt(reader.readLine());

		List<O> output = new ArrayList<O>(sectionLength);
		for (int i = 0; i < sectionLength; i++) {
			output.add(mapper.map(reader.readLine()));
		}
		return output;
	}

	public GramInfo getGramInfo(String word) {
		return null;
	}

	public Set<String> getWordNorm(String word) {
		Set<String> list = norms.get(word.toLowerCase());
		return list == null
			? Collections.<String>emptySet()
			: list;
	}

	public List<Lemma> getLemmas() {
		return lemmas;
	}

	public List<String> buildAllVariations(Lemma lemma) {
		List<Flexion> flexions = allFlexions.get(lemma.getFlexionIndex());
		List<String> variations = new LinkedList<String>();
		if (lemma.getLemma().equals("#")) {
			for (Flexion flexion : flexions) {
				variations.add(flexion.getEnding());
			}
		} else {
			for (Flexion flexion : flexions) {
				variations.add(lemma.getLemma() + flexion.getEnding());
			}
		}
		return variations;
	}
}
