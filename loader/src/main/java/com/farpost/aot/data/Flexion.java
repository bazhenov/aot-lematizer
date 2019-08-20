package com.farpost.aot.data;

public class Flexion {

	public final String source;
	public final int grammarIndex;

	public int lemmaIndex = -1;

	public Flexion(String source, int grammarIndex) {
		this.source = source;
		this.grammarIndex = grammarIndex;
	}
}
