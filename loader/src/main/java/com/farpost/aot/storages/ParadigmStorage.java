package com.farpost.aot.storages;

import com.farpost.aot.func.Mod;
import com.farpost.aot.func.Paradigm;
import com.farpost.aot.readers.MrdReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParadigmStorage {

	private final List<Paradigm> pars = new ArrayList<>();
	private final GrammarStorage gram;

	public ParadigmStorage(final MrdReader reader, final GrammarStorage gram) throws IOException {
		this.gram = gram;
		final var len = reader.readLong();
		for (var i = 0; i < len; ++i) {
			pars.add(new Paradigm(
				Arrays.stream(reader.readLine().split("%"))
					.filter(s -> !s.isBlank())
					.map(this::parseMod)
					.collect(Collectors.toList())
			));
		}
	}

	private static String normalize(String token) {
		return token.toLowerCase().replace('ё', 'е');
	}

	private Mod parseMod(String src) {
			final var args = src.split("\\*");
			final var postfix = normalize(args[0]);
			final var infoIndex = gram.getVariantIndex(args[1]);
			return args.length == 2 ?
				new Mod(infoIndex, null, postfix) :
				new Mod(infoIndex, normalize(args[2]), postfix);
	}

	public Paradigm get(final int index) {
		return pars.get(index);
	}
}
