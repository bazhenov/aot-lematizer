package com.farpost.aot;

/**
 * Минифицированная флексия
 */
class MiniFlexion {

	private final int morphIndex;
	private final int stringIndex;

	MiniFlexion(int stringIndex, int morphIndex) {
		this.morphIndex = morphIndex;
		this.stringIndex = stringIndex;
	}

	int getStringIndex() {
		return stringIndex;
	}

	int getMorphIndex() {
		return morphIndex;
	}
}
