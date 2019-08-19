package com.farpost.aot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Класс инкапсулирует способы создания
 */
public class FlexionFabric {

	/**
	 * Конструирование из готового набора
	 */
	private static Flexion createFlexion(String prefix, String base, String postfix, MorphologyTag[] tags) {
		var sourceBuilder = new StringBuilder();
		if (prefix != null) {
			sourceBuilder.append(prefix);
		}
		if (base.charAt(0) != '#') {
			sourceBuilder.append(base);
		}
		return new Flexion(sourceBuilder.append(postfix).toString(), tags);
	}

	private static String normalize(String token) {
		return token.toLowerCase().replace('ё', 'е');
	}

	/**
	 * Разбор кода из словаря
	 */
	private static Flexion createFlexion(String base, String source, Map<String, MorphologyTag[]> morphMap) {
		var args = source.split("\\*");
		return createFlexion(
			args.length == 2 ? null : normalize(args[2]),
			normalize(base),
			normalize(args[0]),
			morphMap.get(args[1])
		);
	}

	/**
	 * @param base     база
	 * @param paradigm парадигма склонения
	 * @param morphMap морфологический словарь
	 * @return лемма (содержит флексии, первая флексия собственно лемма остальных)
	 */
	static List<Flexion> createLemma(String base, String paradigm, Map<String, MorphologyTag[]> morphMap) {
		return Arrays.stream(paradigm.split("%"))
			.filter(s -> !s.isBlank())
			.map(src ->
				createFlexion(base, src, morphMap)
			)
			.collect(toUnmodifiableList());
	}
}
