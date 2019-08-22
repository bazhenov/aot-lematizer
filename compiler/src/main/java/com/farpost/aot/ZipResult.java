package com.farpost.aot;

import java.util.List;
import java.util.Set;

/**
 * Результат сжатия набора лемм
 */
class ZipResult {

	private final List<Set<MorphologyTag>> morph;
	private final List<String> strings;
	private final List<List<MiniFlexion>> lemmas;

	ZipResult(List<List<MiniFlexion>> lemmas, List<String> strings, List<Set<MorphologyTag>> morph) {
		this.morph = morph;
		this.strings = strings;
		this.lemmas = lemmas;
	}

	List<Set<MorphologyTag>> getMorph() {
		return morph;
	}

	List<String> getStrings() {
		return strings;
	}

	List<List<MiniFlexion>> getLemmas() {
		return lemmas;
	}
}
