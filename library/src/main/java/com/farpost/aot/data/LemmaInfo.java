package com.farpost.aot.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LemmaInfo {

	// индекс леммы в хранилище лемм (по сути, id)
	public final int lemmaIndex;
	// каждый массив - флексия, совпадающая по строке с искомой
	public final List<GrammarInfo[]> flexions = new ArrayList<>();
	// лемма
	public final String lemma;

	public LemmaInfo(final int lemmaIndex, final String lemma) {
		this.lemmaIndex = lemmaIndex;
		this.lemma = lemma;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder()
			.append('<').append(lemma);
		for (final GrammarInfo[] i : flexions) {
			builder.append(", ").append(Arrays.asList(i));
		}
		return builder.append('>').toString();
	}
}
