package com.farpost.aot;

import java.util.Objects;

/**
 * Флексия - лемма (изначальная форма слова) + грамматическая информация об вторичной форме слова
 */
public class Flexion {

	/**
	 * Лемма - исходная форма слова
	 */
	public final String lemma;
	/**
	 * Грамматическая информация в виде массива элементов перечисления.
	 * (Информация именно о словоформе, а не о лемме, от которой было произведено слово).
	 */
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
