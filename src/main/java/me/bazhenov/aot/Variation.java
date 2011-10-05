package me.bazhenov.aot;

public class Variation {

	private final String word;
	private final String ancode;
	private int lemmaIndex;
	private static final int LEMMA_INDEX = -1;

	public Variation(String word, String ancode) {
		this.word = word;
		this.ancode = ancode;
		this.lemmaIndex = LEMMA_INDEX;
	}

	public void setLemmaIndex(int lemmaIndex) {
		this.lemmaIndex = lemmaIndex;
	}

	public String getAncode() {
		return ancode;
	}

	public String getWord() {
		return word;
	}

	public int getLemmaIndex() {
		return lemmaIndex;
	}

	@Override
	public String toString() {
		return "Variation{" +
			"word='" + word + '\'' +
			'}';
	}

	public boolean isLemma() {
		return lemmaIndex == LEMMA_INDEX;
	}
}
