package com.farpost.aot;


import java.util.List;

/**
 * Подкласс флексий добавляющий свойства необходимые только на этапе компиляции
 */
public class CompilerFlexion extends Flexion {

	private final int lemmaId;

	public CompilerFlexion(String string, List<MorphologyTag> tags, Flexion lemma, int lemmaId) {
		super(string, tags, lemma);
		this.lemmaId = lemmaId;
	}

	/**
	 * @return порядковый номер леммы этой флексии в mrd-файле
	 */
	public int getLemmaId() {
		return lemmaId;
	}
}
