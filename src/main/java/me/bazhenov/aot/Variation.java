package me.bazhenov.aot;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Variation {

	private final String word;
	private final String ancode;
	private final int id;
	private int lemmaIndex;
	private static final int LEMMA_INDEX = -1;

	public Variation(String word, String ancode, int id) {
		checkNotNull(ancode);
		checkNotNull(ancode);
		checkArgument(id > 0, "Id should be positive number");
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
			", ancode='" + ancode + '\'' +
			", id=" + id +
			", lemmaIndex=" + lemmaIndex +
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

		return id == variation.id
			&& lemmaIndex == variation.lemmaIndex
			&& ancode.equals(variation.ancode)
			&& word.equals(variation.word);
	}

	@Override
	public int hashCode() {
		int result = word.hashCode();
		result = 31 * result + ancode.hashCode();
		result = 31 * result + id;
		result = 31 * result + lemmaIndex;
		return result;
	}
}
