package com.farpost.aot.mrd;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

	public final Map<String, Integer> ancodeToIndex;

	public Parser(final Map<String, Integer> ancodeToIndex) {
		this.ancodeToIndex = ancodeToIndex;
	}

	public Mod parseMod(final String src) {
		final var args = src.split("\\*");
		final var postfix = args[0];
		final var infoIndex = ancodeToIndex.getOrDefault(args[1], -1);
		return args.length == 2 ? new Mod(postfix, infoIndex) : new Mod(postfix, infoIndex, args[2]);
	}

	public Paradigm parseParadigm(final String src) {
		return new Paradigm(Arrays
			.stream(src.split("%"))
			.filter(s -> !s.isBlank())
			.map(this::parseMod)
			.collect(Collectors.toList())
		);
	}
}
