package com.farpost.aot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Flexion {

	private final String string;
	private final MorphologyTag[] morphologyTags;
	private final Flexion lemma;

	private Flexion(String string, MorphologyTag[] tags, Flexion lemma) {
		this.string = string;
		morphologyTags = tags;
		this.lemma = lemma == null ? this : lemma; // если леммы нет, то лемма - это сама флексия
	}

	public String getString() {
		return string;
	}

	public Flexion getLemma() {
		return lemma;
	}

	public MorphologyTag[] getTags() {
		return morphologyTags;
	}

	/**
	 * Конструирование из готового набора
	 */
	private static Flexion from(String prefix, String base, String postfix, MorphologyTag[] tags, Flexion lemma) {
		var sourceBuilder = new StringBuilder();
		if (prefix != null) {
			sourceBuilder.append(prefix);
		}
		if (base.charAt(0) != '#') {
			sourceBuilder.append(base);
		}
		return new Flexion(sourceBuilder.append(postfix).toString(), tags, lemma);
	}

	private static String normalize(String token) {
		return token.toLowerCase().replace('ё', 'е');
	}

	/**
	 * Разбор кода из словаря
	 */
	private static Flexion from(String base, String source, Map<String, MorphologyTag[]> morphMap, Flexion lemma) {
		var args = source.split("\\*");
		return from(
			args.length == 2 ? null : normalize(args[2]),
			normalize(base),
			normalize(args[0]),
			morphMap.get(args[1]),
			lemma
		);
	}

	/**
	 * Набор флексий (где первая - лемма остальных) из базы + парадигмы склонения
	 */
	static Flexion[] from(String base, String paradigm, Map<String, MorphologyTag[]> morphMap) {
		var sources = Arrays.stream(paradigm.split("%")).filter(s -> !s.isBlank()).collect(toList());
		var lemma = from(base, sources.get(0), morphMap, null);
		return Stream.concat(
			Stream.of(lemma),
			sources.stream().skip(1).map(src -> from(base, src, morphMap, lemma))
		).toArray(Flexion[]::new);
	}
}
