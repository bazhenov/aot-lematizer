package com.farpost.aot.data;


import java.util.ArrayList;
import java.util.List;

public class LemmaInfo {

	// индекс леммы в хранилище лемм (по сути, id)
	public final int lemmaIndex;
	// каждая флексия совпадает по строке с искомой
	public final List<FlexionInfo> flexions = new ArrayList<>();
	// лемма
	public final String lemma;

	public LemmaInfo(final int lemmaIndex, final String lemma) {
		this.lemmaIndex = lemmaIndex;
		this.lemma = lemma;
	}

	// часть речи
	public PartOfSpeech getPartOfSpeech() {
		return flexions.get(0).partOfSpeech;
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
