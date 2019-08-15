package com.farpost.aot.compiler;

import com.farpost.aot.MorphologyTag;

import java.util.Set;

public class Flexion extends com.farpost.aot.Flexion {

	private final int lemmaId;

	public Flexion(String string, Set<MorphologyTag> tags, com.farpost.aot.Flexion lemma, int lemmaId) {
		super(string, tags, lemma);
		this.lemmaId = lemmaId;
	}

	public int getLemmaId() {
		return lemmaId;
	}
}
