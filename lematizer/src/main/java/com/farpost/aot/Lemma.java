package com.farpost.aot;

import java.util.List;

import static java.util.Arrays.asList;

public class Lemma {

	private final Flexion[] arr;

	public Lemma(Flexion[] arr) {
		this.arr = arr;
	}

	public List<MorphologyTag> getMorphology() {
		return arr[0].getTags();
	}

	public String getWord() {
		return arr[0].getWord();
	}

	public List<Flexion> getFlexions() {
		return asList(arr);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{\n");
		for (Flexion flex : arr) {
			result.append("  ").append(flex).append("\n");
		}
		return result.append("}").toString();
	}
}
