package com.farpost.aot.func;

import com.farpost.aot.data.Flexion;

public class Mod {

	private final int grammarIndex;
	private final String pref;
	private final String post;

	public Mod(int grammarIndex, String pref, String post) {
		this.grammarIndex = grammarIndex;
		this.pref = pref;
		this.post = post;
	}

	public Flexion apply(final String base) {
		final var res = new StringBuilder();
		if (pref != null) {
			res.append(pref);
		}
		// Если у слова изменяемая основа, то
		if (!base.startsWith("#")) {
			res.append(base);
		}
		return new Flexion(res.append(post).toString(), grammarIndex);
	}
}
