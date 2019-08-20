package com.farpost.aot;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Флексия - фариант леммы
 */
public class Flexion {

	private final String word;
	private final PartOfSpeech pos;
	private final MorphologyTag[] morphologyTags;

	public Flexion(String word, MorphologyTag[] tags) {
		this.word = word;
		morphologyTags = tags;
		pos = PartOfSpeech.from(morphologyTags);
	}

	public String getWord() {
		return word;
	}

	public List<MorphologyTag> getTags() {
		return asList(morphologyTags);
	}

	public boolean is(MorphologyTag tag) {
		if (tag == null) {
			return true;
		}
		for (MorphologyTag i : morphologyTags) {
			if (i == tag) {
				return true;
			}
		}
		return false;
	}

	public boolean isNot(MorphologyTag tag) {
		return !is(tag);
	}

	public boolean allOf(MorphologyTag... info) {
		return Arrays.stream(info).allMatch(this::is);
	}

	public boolean anyOf(MorphologyTag... info) {
		return Arrays.stream(info).anyMatch(this::is);
	}

	public boolean notAnyOf(MorphologyTag... info) {
		return !anyOf(info);
	}

	@Override
	public String toString() {
		return word + ' ' + Arrays.toString(morphologyTags);
	}

	public PartOfSpeech getPartOfSpeech() {
		return pos;
	}
}
