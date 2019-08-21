package com.farpost.aot.predicates;

import com.farpost.aot.Flexion;

import java.util.function.Predicate;

public class StringEqualTo implements Predicate<Flexion> {

	private final String orig;

	@Override
	public boolean test(Flexion flexion) {
		return flexion.toString().equals(orig);
	}

	public StringEqualTo(String orig) {
		this.orig = orig;
	}
}

