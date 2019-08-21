package com.farpost.aot.predicates;

import com.farpost.aot.Flexion;
import com.farpost.aot.MorphologyTag;

import java.util.function.Predicate;

public class HasTag implements Predicate<Flexion> {

	private final MorphologyTag[] tags;

	public HasTag(MorphologyTag... tags) {
		this.tags = tags;
	}

	@Override
	public boolean test(Flexion flexion) {
		return flexion.hasAllOf(tags);
	}


}
