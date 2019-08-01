package com.farpost.aot;

import java.util.Objects;

// Флексия --
//   лемма + информация о грамматических характеристиках которые были примнены к лемме
public class Flexion {

	public final String lemma;
	public final GrammarInfo[] grammarInfo;

	public Flexion(final String lemma, final GrammarInfo[] grammarInfo) {
		this.lemma = Objects.requireNonNull(lemma);
		this.grammarInfo = Objects.requireNonNull(grammarInfo);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(lemma).append('(');
		for (int i = 0; i < grammarInfo.length; ++i) {
			builder.append(grammarInfo[i]);
			if (i != grammarInfo.length - 1) {
				builder.append(", ");
			}
		}
		return builder.append(')').toString();
	}
}
