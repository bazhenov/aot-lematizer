package com.farpost.aot.mrd;

import static com.farpost.aot.Compiler.*;

public class Flexion {
	public final int flexionHash, uniqueLemmaIndex, grammarInfoIndex;
	public final String sourceString;

	@Override
	public String toString() {
		return String.format("%d -- lemma %d info %d", flexionHash, uniqueLemmaIndex, grammarInfoIndex);
	}

	public Flexion(final int hash, final int lemma, final int grammar, final String sourceString) {

		this.sourceString = sourceString;

		flexionHash = hash;
		uniqueLemmaIndex = lemma;
		grammarInfoIndex = grammar;
	}
}
