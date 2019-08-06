package com.farpost.aot.mrd;

public class Flexion {

	public final int flexionHash, uniqueLemmaIndex, grammarInfoIndex;
	public final String sourceString;

	public Flexion(final int hash, final int lemma, final int grammar, final String sourceString) {
		this.sourceString = sourceString;
		flexionHash = hash;
		uniqueLemmaIndex = lemma;
		grammarInfoIndex = grammar;
	}

	@Override
	public String toString() {
		return String.format("%d -- lemma %d info %d", flexionHash, uniqueLemmaIndex, grammarInfoIndex);
	}
}
