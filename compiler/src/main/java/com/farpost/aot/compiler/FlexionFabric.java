package com.farpost.aot.compiler;

import com.farpost.aot.MorphologyTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FlexionFabric {

	/**
	 * Конструирование из готового набора
	 */
	private static Flexion createFlexion(String prefix, String base, String postfix, Set<MorphologyTag> tags, Flexion lemma, int lemmaId) {
		var sourceBuilder = new StringBuilder();
		if (prefix != null) {
			sourceBuilder.append(prefix);
		}
		if (base.charAt(0) != '#') {
			sourceBuilder.append(base);
		}
		return new Flexion(sourceBuilder.append(postfix).toString(), tags, lemma, lemmaId);
	}

	private static String normalize(String token) {
		return token.toLowerCase().replace('ё', 'е');
	}

	/**
	 * Разбор кода из словаря
	 */
	private static Flexion createFlexion(String base, String source, Map<String, Set<MorphologyTag>> morphMap, Flexion lemma, int lemmaId) {
		var args = source.split("\\*");
		return createFlexion(
			args.length == 2 ? null : normalize(args[2]),
			normalize(base),
			normalize(args[0]),
			morphMap.get(args[1]),
			lemma,
			lemmaId
		);
	}

	/**
	 * Набор флексий (где первая - лемма остальных) из базы + парадигмы склонения
	 */
	static Collection<Flexion> createFlexions(String base, String paradigm, Map<String, Set<MorphologyTag>> morphMap, int id) {
		var sources = Arrays.stream(paradigm.split("%")).filter(s -> !s.isBlank()).collect(toList());
		var lemma = createFlexion(base, sources.get(0), morphMap, null, id);
		return Stream.concat(
			Stream.of(lemma),
			sources.stream().skip(1).map(src -> createFlexion(base, src, morphMap, lemma, id))
		).collect(toList());
	}
}
