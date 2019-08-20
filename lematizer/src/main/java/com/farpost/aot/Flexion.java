package com.farpost.aot;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Flexion {

	// оптимизация
	static HashDictionary db;

	private final int strIndex, tagsIndex;
	private final PartOfSpeech pos;

	public Flexion(int source, int tags) {
		this.strIndex = source;
		this.tagsIndex = tags;
		pos = PartOfSpeech.from(db.allMorphologyTags[tags]);
	}

	public List<MorphologyTag> getTags() {
		return asList(db.allMorphologyTags[tagsIndex]);
	}

	public boolean has(MorphologyTag tag) {
		for (MorphologyTag i : db.allMorphologyTags[tagsIndex]) {
			if (i == tag) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNot(MorphologyTag tag) {
		return !has(tag);
	}

	public boolean hasAllOf(MorphologyTag... tags) {
		return Arrays.stream(tags).allMatch(this::has);
	}

	public boolean hasAnyOf(MorphologyTag... tags) {
		return Arrays.stream(tags).anyMatch(this::has);
	}

	public boolean hasNoneOf(MorphologyTag... tags) {
		return !hasAnyOf(tags);
	}

	@Override
	public String toString() {
		return db.allFlexionStrings[strIndex];
	}

	public PartOfSpeech getPartOfSpeech() {
		return pos;
	}

}
