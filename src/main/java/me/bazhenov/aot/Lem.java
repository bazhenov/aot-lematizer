package me.bazhenov.aot;

import com.google.common.base.Objects;

import java.util.List;

public class Lem {

	private final String word;
	private final List<Flexion> flexions;
	private final PartOfSpeech posTag;

	public Lem(String word, List<Flexion> flexions) {
		this.word = word;
		this.flexions = flexions;
		String posCode = flexions.get(0).getAncode().split(" ", 3)[1];
		try {
			posTag = PosTag.fromString(posCode);
		} catch (Exception e) {
			throw new RuntimeException("Invalid POS: " + word + "/" + posCode, e);
		}
	}

	public String getWord() {
		return word;
	}

	public PartOfSpeech getPosTag() {
		return posTag;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("word", word)
			.toString();
	}
}
