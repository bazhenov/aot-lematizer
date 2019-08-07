package com.farpost.aot.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lemma {

	private final String src;
	private final List<GrammarInfo[]> flexions;

	public Lemma(String src, List<GrammarInfo[]> flexions) {
		this.src = src;
		this.flexions = flexions;
	}

	public String getSource() {
		return src;
	}

	public List<GrammarInfo[]> getFlexions() {
		return flexions;
	}

	@Override
	public String toString() {
		return
			'<' +
				src + flexions.stream().map(Arrays::asList).collect(Collectors.toList())
				+ '>';
	}
}
