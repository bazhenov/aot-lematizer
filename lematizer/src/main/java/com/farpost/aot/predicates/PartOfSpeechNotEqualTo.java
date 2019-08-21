package com.farpost.aot.predicates;

import com.farpost.aot.Flexion;
import com.farpost.aot.PartOfSpeech;

import java.util.function.Predicate;

public class PartOfSpeechNotEqualTo implements Predicate<Flexion> {

	private final PartOfSpeech orig;

	@Override
	public boolean test(Flexion flexion) {
		return flexion.getPartOfSpeech() != orig;
	}

	public PartOfSpeechNotEqualTo(PartOfSpeech orig) {
		this.orig = orig;
	}
}
