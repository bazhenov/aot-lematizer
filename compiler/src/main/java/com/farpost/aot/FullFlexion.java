package com.farpost.aot;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Флексия - фариант леммы
 */
class FullFlexion {

	private final String source;
	private final MorphologyTag[] tags;

	FullFlexion(String source, MorphologyTag[] tags) {
		this.source = source;
		this.tags = tags;
	}

	List<MorphologyTag> getTags() {
		return asList(tags);
	}

	@Override
	public String toString() {
		return source;
	}
}
