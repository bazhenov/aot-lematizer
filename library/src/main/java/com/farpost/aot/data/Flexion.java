package com.farpost.aot.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Флексия -- лемма (изначальная форма слова) + грамматическая информация об вторичной форме слова
 */
public class Flexion {

	/**
	 * Лемма - исходная форма слова.
	 * Иммутабельное поле.
	 */
	public final String lemma;

	/**
	 * Информации об запрошенной словоформе.
	 * Иммутабельное поле.
	 */
	public final Collection<GrammarInfo> allGrammarInfo;

	public Flexion(final String lemma, final GrammarInfo[] allGrammarInfo) {
		this.lemma = Objects.requireNonNull(lemma);
		this.allGrammarInfo = Arrays.asList(Objects.requireNonNull(allGrammarInfo));
	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее
	 * @param info грамматическая характеристика
	 */
	public boolean is(final GrammarInfo info) {
		return allGrammarInfo.contains(info);
	}

	/**
	 * Проверка что флексия характеризуется неким набором грамматической информации
	 *
	 * @param info набор грамматической информации
	 */
	public boolean is(final GrammarInfo... info) {
		return Arrays.stream(info).allMatch(this::is);
	}

	/**
	 * Проверка что флексия не характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее.
	 * @param info грамматическая характеристика
	 */
	public boolean isNot(final GrammarInfo info) {
		return !allGrammarInfo.contains(info);
	}

	/**
	 * Проверка что флексия не характеризуется ни одним элементом
	 * из переданого набора громматической информации
	 *
	 * @param info набор грамматической информации
	 */
	public boolean isNot(final GrammarInfo... info) {
		return Arrays.stream(info).allMatch(this::isNot);
	}

	@Override
	public String toString() {
		return lemma + allGrammarInfo;
	}
}
