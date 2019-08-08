package com.farpost.aot.data;

import java.util.Arrays;
import java.util.List;

public class FlexionInfo {

	public final PartOfSpeech partOfSpeech;
	private final GrammarTag[] allGrammarTags;

	public FlexionInfo(final GrammarTag[] allGrammarTags) {
		this.partOfSpeech = PartOfSpeech.from(allGrammarTags);
		this.allGrammarTags = allGrammarTags;
	}

	/**
	 * Возвращает иммутабельную коллекцию тегов
	 *
	 * @return список тегов
	 */
	public List<GrammarTag> getAllTags() {
		return Arrays.asList(allGrammarTags);

	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean is(final GrammarTag info) {
		for (GrammarTag i : allGrammarTags) {
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
	public boolean is(final GrammarTag... info) {
		return Arrays.stream(info).allMatch(this::is);
	}

	/**
	 * Проверка что флексия не характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее.
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean isNot(final GrammarTag info) {
		return !is(info);
	}

	/**
	 * Проверка что флексия не характеризуется ни одним элементом
	 * из переданого набора громматической информации
	 *
	 * @param info набор грамматической информации
	 */
	public boolean isNot(final GrammarTag... info) {
		return Arrays.stream(info).allMatch(this::isNot);
	}

	@Override
	public String toString() {
		return Arrays.toString(allGrammarTags);
	}
}
