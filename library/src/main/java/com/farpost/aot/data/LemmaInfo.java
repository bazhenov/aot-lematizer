package com.farpost.aot.data;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

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

	public PartOfSpeech getPartOfSpeech() {
		return flexions.get(0).getPartOfSpeech();
	}

	public int getId() {
		return lemmaIndex;
	}

	public Collection<FlexionInfo> getFlexions() {
		return flexions;
	}

	public String getLemma() {
		return lemma;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder()
			.append('<').append(lemma);
		for (final FlexionInfo i : flexions) {
			builder.append(", ").append(i.toString());
		}
		return builder.append('>').toString();
	}
}
