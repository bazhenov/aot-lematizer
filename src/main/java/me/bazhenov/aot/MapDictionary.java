package me.bazhenov.aot;

import com.google.common.base.Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.readLines;
import static java.lang.Integer.parseInt;
import static me.bazhenov.aot.TernaryTreeDictionary.readSection;

/**
 * Имплементация словаря на основе хранения лексем в нескольких {@link HashMap}
 * с использованием избыточной информации (такие как все основы словаря) для обеспечения быстрой выборки. <br />
 *
 * {@link #lookupWord(String)} не модифицирует состояние внутренних объектов, и как следствие потокобезопасный.
 */
public class MapDictionary implements Dictionary {

	private final Set<String> allPrefixes = new HashSet<>();
	private final Set<String> allBases = new HashSet<>();
	private final Set<String> allEndings = new HashSet<>();

	private final LemmaRepository lemmaRepository;

	private MapDictionary() throws IOException {
		this.lemmaRepository = new LemmaRepository();

		InputStream is = getClass().getResourceAsStream("/mrd");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8));
		TabFileReader tabReader = new TabFileReader();
		List<String> tabDescriptors = readLines(getClass().getResource("/tab"), UTF_8, tabReader);
		Map<String, Integer> tabDescriptorsMapping = tabReader.getMapping();

		Map<Integer, Set<String>> flexionPrefixes = new HashMap<>();
		Map<Integer, Set<String>> flexionEndings = new HashMap<>();
		List<List<Flexion>> allFlexions = readSection(reader, new FlexionFunction(tabDescriptors,
			tabDescriptorsMapping,
			flexionEndings,
			flexionPrefixes
		));

		readSection(reader, null); // accentual models
		readSection(reader, null); // user sessions
		readSection(reader, null); // prefix sets

		readSection(reader, input -> {
			String[] parts = input.split(" ");
			String base = parts[0].toLowerCase().replace("ё", "е").replace("#", "");
			int flexionIndex = parseInt(parts[1]);
			List<Flexion> flexions = allFlexions.get(flexionIndex);

			Lemma l = new Lemma(base, flexions);
			lemmaRepository.insert(l);

			l.setEndings(flexionEndings.get(flexionIndex));
			l.setPrefixes(flexionPrefixes.get(flexionIndex));
			allBases.add(l.getBase());

			return null;
		});
	}

	public static MapDictionary loadDictionary() {
		try {
			return new MapDictionary();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Set<Lemma> lookupWord(String word) {
		String lowercaseWord = word.toLowerCase().replaceAll("ё", "е");
		return IntStream.range(0, word.length())
			.boxed()
			.filter(i -> allPrefixes.contains(lowercaseWord.substring(0, i)))
			.map(i -> lookupWithoutPrefix(lowercaseWord.substring(0, i), lowercaseWord.substring(i, word.length())))
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());

	}

	private Set<Lemma> lookupWithoutPrefix(String preffix, String sufix) {
		Map<String, String> prefixesPostfixes = IntStream.rangeClosed(0, sufix.length())
			.boxed()
			.filter(i -> allBases.contains(sufix.substring(0, i)) &&
				allEndings.contains(sufix.substring(i, sufix.length()))
			)
			.collect(Collectors.toMap(
				index -> sufix.substring(0, index),
				index -> sufix.substring(index, sufix.length())
			));
		Set<Lemma> byBaseIn = lemmaRepository.findByBaseIn(prefixesPostfixes.keySet());
		return byBaseIn.stream()
			.filter(l -> (preffix == null || l.getPrefixes().contains(preffix)) &&
				l.getEndings().contains(prefixesPostfixes.get(l.getBase())))
			.collect(Collectors.toSet());
	}

	private class FlexionFunction implements Function<String, List<Flexion>> {

		private final List<String> tabDescriptors;
		private final Map<String, Integer> tabDescriptorsMapping;
		private final Map<Integer, Set<String>> flexionPrefixes;
		private final Map<Integer, Set<String>> flexionEndings;
		private int flexionIndex = 0;

		public FlexionFunction(List<String> tabDescriptors,
													 Map<String, Integer> tabDescriptorsMapping,
													 Map<Integer, Set<String>> flexionEndings,
													 Map<Integer, Set<String>> flexionPrefixes) {
			this.tabDescriptors = checkNotNull(tabDescriptors);
			this.tabDescriptorsMapping = checkNotNull(tabDescriptorsMapping);
			this.flexionEndings = checkNotNull(flexionEndings);
			this.flexionPrefixes = checkNotNull(flexionPrefixes);
		}

		public List<Flexion> apply(String input) {
			List<Flexion> flexions = newArrayList();
			Set<String> prefixes = new HashSet<>();
			Set<String> endings = new HashSet<>();
			for (String flexion : input.split("%")) {
				if (flexion.isEmpty()) {
					continue;
				}
				String[] parts = flexion.split("\\*");
				String ancode = parts[1].substring(0, 2);
				String morhTag = tabDescriptors.get(tabDescriptorsMapping.get(ancode));
				String prefix = parts.length > 2 ? parts[2].toLowerCase().replace("ё", "е") : "";
				String affix = parts[0].toLowerCase().replace("ё", "е");
				Flexion f = new Flexion(affix, prefix, morhTag);
				flexions.add(f);

				prefixes.add(prefix);
				endings.add(affix);
				allPrefixes.add(prefix);
				allEndings.add(affix);
			}
			flexionEndings.put(flexionIndex, endings);
			flexionPrefixes.put(flexionIndex, prefixes);
			flexionIndex++;

			return flexions;
		}
	}

}
