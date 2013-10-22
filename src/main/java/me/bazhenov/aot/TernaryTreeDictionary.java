package me.bazhenov.aot;

import com.google.common.base.Function;
import com.google.common.io.LineProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.readLines;
import static java.lang.Integer.parseInt;

public class TernaryTreeDictionary implements Dictionary {

	private final TernarySearchTree<IntArrayList> postfixTree = new TernarySearchTree<IntArrayList>();
	private final TernarySearchTree<IntArrayList> prefixTree = new TernarySearchTree<IntArrayList>();
	private final List<Lemma> lemmas = newArrayList();
	private final List<List<Flexion>> allFlexions;

	private TernaryTreeDictionary() throws IOException {
		InputStream is = getClass().getResourceAsStream("/mrd");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		TabFileReader tabReader = new TabFileReader();
		List<String> tabDescriptors = readLines(getResource("tab"), UTF_8, tabReader);
		Map<String, Integer> tabDescriptorsMapping = tabReader.getMapping();

		allFlexions = readSection(reader, new FlexionFunction(tabDescriptors, tabDescriptorsMapping));
		readSection(reader, null); // accentual models
		readSection(reader, null); // user sessions
		readSection(reader, null); // prefix sets

		final AtomicInteger refId = new AtomicInteger();
		readSection(reader, new Function<String, Void>() {
			@Override
			public Void apply(String input) {
				String[] parts = input.split(" ");
				String prefix = parts[0].toLowerCase().replace("ё", "е").replace("#", "");
				int flexionIndex = parseInt(parts[1]);
				List<Flexion> flexions = allFlexions.get(flexionIndex);

				IntArrayList prefixVariations = prefixTree.get(prefix);
				if (prefixVariations == null) {
					prefixVariations = new IntArrayList(1);
					prefixTree.insert(prefix, prefixVariations);
				}

				int wordId = refId.getAndIncrement();
				prefixVariations.add(wordId);

				Lemma l = new Lemma(prefix, flexions);
				lemmas.add(l);
				checkState(lemmas.size() == wordId + 1);

				Set<String> visitedAffixes = newHashSet();
				for (Flexion f : flexions) {
					String affix = reverse(f.getEnding());
					if (visitedAffixes.add(affix)) {
						IntArrayList affixPostingList = postfixTree.get(affix);
						if (affixPostingList == null) {
							affixPostingList = new IntArrayList(1);
							postfixTree.insert(affix, affixPostingList);
						}
						affixPostingList.add(wordId);
					}
				}
				return null;
			}
		});
		reader.close();
	}

	public static TernaryTreeDictionary loadDictionary() throws IOException {
		return new TernaryTreeDictionary();
	}

	private static String reverse(String s) {
		return new StringBuffer(s).reverse().toString();
	}

	@Override
	public Set<Lemma> lookupWord(String word) {
		Map<String, IntArrayList> prefixLookup = prefixTree.findAllInPath(word);
		Map<String, IntArrayList> postfixLookup = postfixTree.findAllInPath(reverse(word));

		Set<Lemma> result = newHashSet();

		for (Map.Entry<String, IntArrayList> i : prefixLookup.entrySet()) {
			String affix = reverse(word.substring(i.getKey().length()));
			if (postfixLookup.containsKey(affix)) {
				IntArrayList a = i.getValue();
				IntArrayList b = postfixLookup.get(affix);
				IntArrayList merged = mergeIntersect(a, b);
				for (int lemmaId : merged.toIntArray()) {
					result.add(lemmas.get(lemmaId));
				}
			}
		}

		return result;
	}

	public static IntArrayList mergeIntersect(IntArrayList a, IntArrayList b) {
		IntArrayList result = new IntArrayList();

		int iA = 0, iB = 0;
		while (iA < a.size() && iB < b.size()) {
			if (a.getInt(iA) < b.getInt(iB))
				iA++;
			else if (a.getInt(iA) > b.getInt(iB))
				iB++;
			else {
				result.add(b.getInt(iB));
				iA++;
				iB++;
			}
		}
		return result;
	}

	public static <O> List<O> readSection(BufferedReader reader, Function<String, O> mapper) throws IOException {
		int sectionLength = parseInt(reader.readLine());

		if (mapper != null) {
			List<O> output = newArrayListWithCapacity(sectionLength);
			for (int i = 0; i < sectionLength; i++) {
				output.add(mapper.apply(reader.readLine()));
			}
			return output;
		} else {
			for (int i = 0; i < sectionLength; i++) {
				reader.readLine();
			}
			return null;
		}
	}
}

class FlexionFunction implements Function<String, List<Flexion>> {

	private final List<String> tabDescriptors;
	private final Map<String, Integer> tabDescriptorsMapping;

	public FlexionFunction(List<String> tabDescriptors, Map<String, Integer> tabDescriptorsMapping) {
		this.tabDescriptors = checkNotNull(tabDescriptors);
		this.tabDescriptorsMapping = checkNotNull(tabDescriptorsMapping);
	}

	public List<Flexion> apply(String input) {
		List<Flexion> flexions = newArrayList();
		for (String flexion : input.split("%")) {
			if (flexion.isEmpty())
				continue;
			String[] parts = flexion.split("\\*");
			String ancode = parts[1].substring(0, 2);
			String morhTag = tabDescriptors.get(tabDescriptorsMapping.get(ancode));
			flexions.add(new Flexion(parts[0].toLowerCase().replace("ё", "е"), morhTag));
		}
		return flexions;
	}
}

class TabFileReader implements LineProcessor<List<String>> {

	private final List<String> tabDescriptors = newArrayList();
	private final Map<String, Integer> mapping = newHashMap();

	@Override
	public boolean processLine(String line) throws IOException {
		line = line.trim();
		if (line.isEmpty() || line.startsWith("//"))
			return true;
		String[] parts = line.split(" ", 2);
		String name = parts[0];
		String descriptor = parts[1];
		mapping.put(name, tabDescriptors.size());
		tabDescriptors.add(descriptor);
		return true;
	}

	@Override
	public List<String> getResult() {
		return tabDescriptors;
	}

	Map<String, Integer> getMapping() {
		return mapping;
	}
}