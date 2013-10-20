package me.bazhenov.aot;

import com.google.common.base.Objects;

import java.util.List;

public class Lem {

	private final String word;
	private final List<Flexion> flexions;

	public Lem(String word, List<Flexion> flexions) {
		this.word = word;
		this.flexions = flexions;
	}

	public String getWord() {
		return word;
	}

	public String getPosTag() {
		return null;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("word", word)
			.toString();
	}
}
