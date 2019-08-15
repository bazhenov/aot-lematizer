package com.farpost.aot;

import java.util.Set;

public class Flexion {
	private final String string;
	private final Set<MorphologyTag> morphologyTags;
	private final Flexion lemma;

	public Flexion(String string, Set<MorphologyTag> tags, Flexion lemma) {
		this.string = string;
		morphologyTags = tags;
		this.lemma = lemma == null ? this : lemma; // если леммы нет, то лемма - это сама флексия
	}

	public String getString() {
		return string;
	}

	public Flexion getLemma() {
		return lemma;
	}

	public Set<MorphologyTag> getTags() {
		return morphologyTags;
	}
}
