package com.farpost.aot.predicates;

import com.farpost.aot.Flexion;
import com.farpost.aot.MorphologyTag;

import java.util.function.Predicate;

public class HasNotTag implements Predicate<Flexion> {

	private final MorphologyTag[] tags;

	public HasNotTag(MorphologyTag... tags) {
		this.tags = tags;
	}

	@Override
	public boolean test(Flexion flexion) {
		return flexion.hasNoneOf(tags);
	}
}
