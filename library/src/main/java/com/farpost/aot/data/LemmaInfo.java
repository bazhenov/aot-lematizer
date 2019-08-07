package com.farpost.aot.data;

import java.util.List;

/**
 * Основенной класс данных. Результат поиска.
 * Инкапсулирует одну лемму и все ее флексии которые (!) совпадают с изначально запрашиваемой флексией (lookup)
 */
public class LemmaInfo {

	private final String lemma;
	private final List<FlexionInfo> flexionsInfo;

	public LemmaInfo(final String lemma, final List<FlexionInfo> flexionsInfo) {
		this.lemma = lemma;
		this.flexionsInfo = flexionsInfo;
	}

	/**
	 * @return строка леммы
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @return Все флексии леммы (совпавшие с запрошенной изначально)
	 */
	public List<FlexionInfo> getAllFlexions() {
		return flexionsInfo;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder()
			.append('<').append(lemma);

		for (final FlexionInfo i : flexionsInfo) {
			builder.append(", ").append(i);
		}

		return builder.append('>').toString();
	}
}
