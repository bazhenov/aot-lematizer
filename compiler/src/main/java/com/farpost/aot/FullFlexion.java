package com.farpost.aot;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Флексия - фариант леммы
 */
public class FullFlexion {

	private final String source;
	private final PartOfSpeech pos;
	private final MorphologyTag[] tags;

	public FullFlexion(String source, MorphologyTag[] tags) {
		this.source = source;
		this.tags = tags;
		pos = PartOfSpeech.from(this.tags);
	}

	public List<MorphologyTag> getTags() {
		return asList(tags);
	}

	public boolean has(MorphologyTag tag) {
		for (MorphologyTag i : tags) {
			if (i == tag) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNot(MorphologyTag tag) {
		return !has(tag);
	}

	public boolean hasAllOf(MorphologyTag... info) {
		return Arrays.stream(info).allMatch(this::has);
	}

	public boolean hasAnyOf(MorphologyTag... info) {
		return Arrays.stream(info).anyMatch(this::has);
	}

	public boolean hasNoneOf(MorphologyTag... info) {
		return !hasAnyOf(info);
	}

	@Override
	public String toString() {
		return source;
	}

	public PartOfSpeech getPartOfSpeech() {
		return pos;
	}
}
