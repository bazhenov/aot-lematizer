package com.farpost.aot.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
	public final Collection<GrammarInfo> grammarInfo;


	/**
	 * Является ли флексия некой грамматической характеристикой
	 * Будь то падеж, склонение, род, число и так далее
	 * @param info грамматическая характеристика
	 * @return является или нет
	 */
	public boolean is(final GrammarInfo info) {
		return grammarInfo.contains(info);
	}

	public Flexion(final String lemma, final GrammarInfo[] grammarInfo) {
		this.lemma = Objects.requireNonNull(lemma);
		this.grammarInfo = Arrays.asList(Objects.requireNonNull(grammarInfo));
	}

	@Override
	public String toString() {
		return lemma + grammarInfo;
	}
}
