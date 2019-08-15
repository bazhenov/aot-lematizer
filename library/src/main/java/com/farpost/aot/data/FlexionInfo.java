package com.farpost.aot.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlexionInfo {

	private final PartOfSpeech partOfSpeech;
	private final MorphologyTag[] allMorphologyTags;

	public FlexionInfo(final MorphologyTag[] allMorphologyTags) {
		this.partOfSpeech = PartOfSpeech.from(allMorphologyTags);
		this.allMorphologyTags = allMorphologyTags;
	}

	/**
	 * @return часть речи
	 */
	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * @return множество морфологических тегов
	 */
	public Set<MorphologyTag> getAllTags() {
		return new HashSet<>(Arrays.asList(allMorphologyTags));
	}

	/**
	 * Проверка что флексия характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean is(final MorphologyTag info) {
		for (MorphologyTag i : allMorphologyTags) {
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
	public boolean allOf(final MorphologyTag... info) {
		return Arrays.stream(info).allMatch(this::is);
	}

	/**
	 * Проверка что флексия не характеризуется некой грамматической информацией,
	 * будь то падеж, склонение, род, число и так далее.
	 *
	 * @param info грамматическая характеристика
	 */
	public boolean isNot(final MorphologyTag info) {
		return !is(info);
	}

	/**
	 * Проверка что флексия не характеризуется ни одним элементом
	 * из переданого набора громматической информации
	 *
	 * @param info набор грамматической информации
	 */
	public boolean notAnyOf(final MorphologyTag... info) {
		return Arrays.stream(info).allMatch(this::isNot);
	}

	@Override
	public String toString() {
		return Arrays.toString(allMorphologyTags);
	}
}
