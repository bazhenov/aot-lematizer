package me.bazhenov.aot;

public class Variation {

	private final String word;
	private final String ancode;
	private final int id;
	private int lemmaIndex;
	private static final int LEMMA_INDEX = -1;

	public Variation(String word, String ancode, int id) {
		this.word = word;
		this.ancode = ancode;
		this.id = id;
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
		return lemmaIndex == id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Variation variation = (Variation) o;

		if (id != variation.id) return false;
		if (lemmaIndex != variation.lemmaIndex) return false;
		if (ancode != null ? !ancode.equals(variation.ancode) : variation.ancode != null) return false;
		if (word != null ? !word.equals(variation.word) : variation.word != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = word != null ? word.hashCode() : 0;
		result = 31 * result + (ancode != null ? ancode.hashCode() : 0);
		result = 31 * result + id;
		result = 31 * result + lemmaIndex;
		return result;
	}
}
