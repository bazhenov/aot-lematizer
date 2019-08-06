package com.farpost.aot.data;

import java.util.Arrays;
import java.util.Collection;
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
	public final Collection<GrammarInfo> allGrammarInfo;

	public Flexion(final String lemma, final GrammarInfo[] allGrammarInfo) {
		this.lemma = Objects.requireNonNull(lemma);
		this.allGrammarInfo = Arrays.asList(Objects.requireNonNull(allGrammarInfo));
	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией
	 * Будь то падеж, склонение, род, число и так далее
	 * @param info грамматическая характеристика
	 */
	public boolean is(final GrammarInfo info) {
		return allGrammarInfo.contains(info);
	}

	/**
	 * Проверка что флексия не характеризуется некой грамматической информацией
	 * Будь то падеж, склонение, род, число и так далее
	 * @param info грамматическая характеристика
	 */
	public boolean isNot(final GrammarInfo info) {
		return !allGrammarInfo.contains(info);
	}

	@Override
	public String toString() {
		return lemma + allGrammarInfo;
	}
}
