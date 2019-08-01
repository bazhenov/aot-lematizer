package com.farpost.aot.mrd;


/// Класс отвечает за модификацию основы слова

import java.util.Map;

public class Mod {

	public final String postfix, prefix;
	public final int grammarInfoLineIndex;

	public Mod(final String postfix, final int infoIndex, final String prefix) {
		this.grammarInfoLineIndex = infoIndex;
		this.postfix = postfix.toLowerCase().replace('ё', 'е');
		this.prefix =  prefix == null? null: prefix.toLowerCase().replace('ё', 'е');
	}

	public Mod(final String postfix, final int infoIndex) {
		this(postfix, infoIndex, null);
	}

	public static class ApplyResult {
		public final int infoLineIndex;
		public final String flexion;

		private ApplyResult(final String flexion, final int infoLineIndex) {
			this.flexion = flexion;
			this.infoLineIndex = infoLineIndex;
		}
	}

	public ApplyResult apply(final String base) {
		final StringBuffer res = new StringBuffer();
		if (prefix != null) {
			res.append(prefix);
		}
		// Если у слова изменяемая основа, то
		if(!base.startsWith("#")) {
			res.append(base.toLowerCase().replace('ё', 'е'));
		}
		return new ApplyResult(res.append(postfix).toString(), grammarInfoLineIndex);
	}
}
