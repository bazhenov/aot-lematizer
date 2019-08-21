package com.farpost.aot;

import com.farpost.aot.predicates.*;

import java.util.function.Predicate;


public final class FlexionPredicate {

	public static Predicate<Flexion> hasNotTag(MorphologyTag tag) {
		return new HasNotTag(tag);
	}

	public static Predicate<Flexion> hasTag(MorphologyTag tag) {
		return new HasTag(tag);
	}

	public static Predicate<Flexion> partOfSpeechEqualTo(PartOfSpeech pos) {
		return new PartOfSpeechEqualTo(pos);
	}

	public static Predicate<Flexion> partOfSpeechNotEqualTo(PartOfSpeech pos) {
		return new PartOfSpeechNotEqualTo(pos);
	}

	public static Predicate<Flexion> stringEqualTo(String flexionString) {
		return new StringEqualTo(flexionString);
	}

	public static Predicate<Flexion> stringNotEqualTo(String flexionString) {
		return new StringNotEqualTo(flexionString);
	}
}
