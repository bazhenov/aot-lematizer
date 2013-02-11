package me.bazhenov.aot;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.io.Closeables.close;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.Integer.parseInt;

public class DictionaryDumper {

	@Parameter(names = "-mrd", description = "Path to MRD file")
	public String mrd;

	@Parameter(names = "-tab", description = "Path to TAB file")
	public String tab;

	@Parameter(names = "-out", description = "Path to output file")
	public String out;

	public static void main(String[] args) throws IOException {
		DictionaryDumper dumper = new DictionaryDumper();
		new JCommander(dumper, args);
		InputStream mrd = new BufferedInputStream(new FileInputStream(dumper.mrd));
		InputStream tab = new BufferedInputStream(new FileInputStream(dumper.tab));
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dumper.out));
		try {
			dumper.dumpDictionary(mrd, tab, output);
		} finally {
			closeQuietly(mrd);
			closeQuietly(tab);
			close(output, false);
		}
	}

	public void dumpDictionary(InputStream mrd, InputStream tab, OutputStream out) throws IOException {
		BufferedInputStream is = new BufferedInputStream(mrd);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<List<Flexion>> allFlexions = readSection(reader, new FlexionMapper());
		readSection(reader, new NullMapper()); // accentual models
		readSection(reader, new NullMapper()); // user sessions
		readSection(reader, new NullMapper()); // prefix sets
		List<Lemma> lemmas = readSection(reader, new LemmaMapper());
		reader.close();
		is.close();

		Map<String, GramInfo> grams = Dictionary.buildGramInfo(tab);
		tab.close();

		AtomicInteger sequence = new AtomicInteger(1);
		List<Variation> allTheWords = newArrayList();
		Map<Integer, Variation> lemmaVs = new HashMap<Integer, Variation>();
		for (Lemma l : lemmas) {
			List<Variation> lemmaVariations = buildAllVariations(l, allFlexions.get(l.getFlexionIndex()), sequence);
			Variation lemmaVariation = lemmaVariations.get(0);
			lemmaVs.put(lemmaVariation.getId(), lemmaVariation);

			for (Variation v : lemmaVariations) {
				v.setLemmaIndex(lemmaVariation.getId());
				allTheWords.add(v);
			}
		}
		Collections.sort(allTheWords, new VariationComparator());

		PrintWriter writer = new PrintWriter(out);
		for (Variation v : allTheWords) {
			writer.println(v.getId() + "\t" + v.getWord() + "\t" + v.getLemmaId() + "\t" + lemmaVs.get(v.getLemmaId()).getWord() +
				"\t" + grams.get(v.getAncode()).getDescription());
		}
		writer.flush();
		writer.close();
	}

	public static List<Variation> buildAllVariations(Lemma lemma, List<Flexion> flexions, AtomicInteger sequence) {
		List<Variation> variations = newLinkedList();
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

	private static <O> List<O> readSection(BufferedReader reader, Mapper<String, O> mapper) throws IOException {
		int sectionLength = parseInt(reader.readLine());

		List<O> output = new ArrayList<O>(sectionLength);
		for (int i = 0; i < sectionLength; i++) {
			output.add(mapper.map(reader.readLine()));
		}
		return output;
	}
}
