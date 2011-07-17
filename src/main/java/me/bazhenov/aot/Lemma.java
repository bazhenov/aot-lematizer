package me.bazhenov.aot;

public class Lemma {

	private final String lemma;
	private final int flexionIndex;

	public Lemma(String lemma, int flexionIndex) {
		this.lemma = lemma;
		this.flexionIndex = flexionIndex;
	}

	public String getLemma() {
		return lemma;
	}

	public int getFlexionIndex() {
		return flexionIndex;
	}
}
