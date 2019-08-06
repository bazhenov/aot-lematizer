package com.farpost.aot.mrd;

import java.util.List;

public class UniqueLemmasCollector {

	public final List<String> uniqueLemmas;

	public UniqueLemmasCollector(final List<String> ref) {
		uniqueLemmas = ref;
	}

	public String get(final int i) {
		return uniqueLemmas.get(i);
	}

	// принимает лемму, возвращает ее индекс в массиве лемм
	public int getIndexOfLemma(final String lemma) {

		// Этот код склеивал одинаковые леммы
		/*final int fix = uniqueLemmas.indexOf(lemma);
		if (fix == -1) {
			uniqueLemmas.add(lemma);
			return uniqueLemmas.size() - 1;
		}
		return fix;*/

		// Это новый код, он ничего не склеивает,
		// ибо совпадающие леммы могут иметь несовпадающие наборы флексий
		uniqueLemmas.add(lemma);
		return uniqueLemmas.size() - 1;
	}
}
