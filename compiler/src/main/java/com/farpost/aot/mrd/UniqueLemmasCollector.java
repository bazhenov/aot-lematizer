package com.farpost.aot.mrd;

import java.util.ArrayList;
import java.util.List;

public class UniqueLemmasCollector {
	public final List<String> uniqueLemmas;

	public UniqueLemmasCollector(final List<String> ref) {
		uniqueLemmas = ref;
	}

	public String get(final int i) {
		return uniqueLemmas.get(i);
	}

	// принимает лемму, возвращает ее индекс в массиве уникальных лемм
	public int getIndexOfLemma(final String lemma) {
		final int fix = uniqueLemmas.indexOf(lemma);
		if(fix == -1) {
			uniqueLemmas.add(lemma);
			return uniqueLemmas.size() - 1;
		}
		return fix;
	}
}
