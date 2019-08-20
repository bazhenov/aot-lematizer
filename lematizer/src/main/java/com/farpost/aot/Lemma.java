package com.farpost.aot;

import java.util.List;

import static java.util.Arrays.asList;

public class Lemma {

	private final Flexion[] arr;

	public Lemma(Flexion[] arr) {
		this.arr = arr;
	}

	public List<Flexion> getFlexions() {
		return asList(arr);
	}

	public String getWord() {
		return arr[0].getWord();
	}

	public List<MorphologyTag> getTags() {
		return arr[0].getTags();
	}

	public boolean is(MorphologyTag tag) {
		return arr[0].is(tag);
	}

	public boolean isNot(MorphologyTag tag) {
		return arr[0].isNot(tag);
	}

	public boolean allOf(MorphologyTag... tag) {
		return arr[0].allOf(tag);
	}

	public boolean anyOf(MorphologyTag... tag) {
		return arr[0].anyOf(tag);
	}

	public boolean notAnyOf(MorphologyTag... tag) {
		return arr[0].notAnyOf(tag);
	}

	@Override
	public String toString() {
		return arr[0].toString();
	}

	public PartOfSpeech getPartOfSpeech() {
		return arr[0].getPartOfSpeech();
	}
}
