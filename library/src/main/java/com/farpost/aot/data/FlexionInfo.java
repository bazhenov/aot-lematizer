package com.farpost.aot.data;

import java.util.Arrays;

public class FlexionInfo {

	public final GrammarInfo[] allGrammarInfo;

	public FlexionInfo(final GrammarInfo[] allGrammarInfo) {
		this.allGrammarInfo = allGrammarInfo;
	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean is(final GrammarInfo info) {
		for (GrammarInfo i : allGrammarInfo) {
			if (i == info) {
				return true;
			}
		}
		return false;
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
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean isNot(final GrammarInfo info) {
		return !is(info);
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
		return Arrays.toString(allGrammarInfo);
	}
}
