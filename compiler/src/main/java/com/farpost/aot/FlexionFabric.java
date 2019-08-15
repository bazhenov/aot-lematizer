package com.farpost.aot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FlexionFabric {

	/**
	 * Конструирование из готового набора
	 */
	private static CompilerFlexion createFlexion(String prefix, String base, String postfix, List<MorphologyTag> tags, CompilerFlexion lemma, int lemmaId) {
		var sourceBuilder = new StringBuilder();
		if (prefix != null) {
			sourceBuilder.append(prefix);
		}
		if (base.charAt(0) != '#') {
			sourceBuilder.append(base);
		}
		return new CompilerFlexion(sourceBuilder.append(postfix).toString(), tags, lemma, lemmaId);
	}

	private static String normalize(String token) {
		return token.toLowerCase().replace('ё', 'е');
	}

	/**
	 * Разбор кода из словаря
	 */
	private static CompilerFlexion createFlexion(String base, String source, Map<String, List<MorphologyTag>> morphMap, CompilerFlexion lemma, int lemmaId) {
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
	static List<CompilerFlexion> createFlexions(String base, String paradigm, Map<String, List<MorphologyTag>> morphMap, int id) {
		var sources = Arrays.stream(paradigm.split("%")).filter(s -> !s.isBlank()).collect(toList());
		var lemma = createFlexion(base, sources.get(0), morphMap, null, id);
		return Stream.concat(
			Stream.of(lemma),
			sources.stream().skip(1).map(src -> createFlexion(base, src, morphMap, lemma, id))
		).collect(toList());
	}
}
