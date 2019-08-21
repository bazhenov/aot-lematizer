package com.farpost.aot;

/**
 * Минифицированная флексия
 */
public class MiniFlexion {

	private final int morphIndex;
	private final int stringIndex;

	public MiniFlexion(int stringIndex, int morphIndex) {
		this.morphIndex = morphIndex;
		this.stringIndex = stringIndex;
	}

	public int getStringIndex() {
		return stringIndex;
	}

	public int getMorphIndex() {
		return morphIndex;
	}
}
