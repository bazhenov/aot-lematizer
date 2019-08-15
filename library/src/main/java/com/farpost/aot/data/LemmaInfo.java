package com.farpost.aot.data;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class LemmaInfo {

	// индекс леммы в хранилище лемм (по сути, id)
	private final int lemmaIndex;
	// каждая флексия совпадает по строке с искомой
	private final List<FlexionInfo> flexions = new ArrayList<>();
	// лемма
	private final String lemma;

	public LemmaInfo(final int lemmaIndex, final String lemma) {
		this.lemmaIndex = lemmaIndex;
		this.lemma = requireNonNull(lemma);
	}

	/**
	 * @return id леммы
	 */
	public int getId() {
		return lemmaIndex;
	}

	/**
	 * @return множество флексий
	 */
	public Collection<FlexionInfo> getFlexions() {
		return flexions;
	}

	/**
	 * @return исходная форма слова
	 */
	public String getLemma() {
		return lemma;
	}

	@Override
	public String toString() {
		String flexions = this.flexions.stream()
			.map(Object::toString)
			.collect(joining(", "));
		return String.format("<%s %s>", lemma, flexions);
	}
}
