package com.farpost.aot.data;

import java.util.Arrays;
import java.util.List;

/**
 * Класс содержит информацию о флексии (но не саму флексию)
 */
public class FlexionInfo {

	private final List<GrammarInfo> allGrammarInfo;

	public FlexionInfo(final GrammarInfo[] allGrammarInfo) {
		this.allGrammarInfo = Arrays.asList(allGrammarInfo);
	}

	/**
	 * Получить всю грамматическую информацию
	 *
	 * @return список перечислений GrammarInfo
	 */
	public List<GrammarInfo> getAllGrammarInfo() {
		return allGrammarInfo;
	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее
	 *
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
	 *
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
		return allGrammarInfo.toString();
	}
}
