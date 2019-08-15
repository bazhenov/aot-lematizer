package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.data.MorphologyTag;
import com.farpost.aot.readers.MrdReader;
import com.farpost.aot.storages.GrammarStoragePrev;
import com.farpost.aot.storages.ParadigmStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;

import static java.util.stream.Collectors.joining;

/**
 * Главное хранилище инкапсулирует остальные
 */
public class FlexionStorage {

	private final GrammarStoragePrev gram = new GrammarStoragePrev();
	private final List<String> lemmas = new ArrayList<>();
	private final List<Flexion> flexions = new ArrayList<>();

	public FlexionStorage() throws IOException {
		final var reader = new MrdReader();
		final var pars = new ParadigmStorage(reader, gram);
		reader.skipSection();
		reader.skipSection();
		reader.skipSection();
		final var len = reader.readLong();
		for (var i = 0; i < len; ++i) {
			final var line = reader.readLine();
			// фильтр метапостфиксов
			if (line.startsWith("-")) {
				continue;
			}
			final var toks = line.split(" ");
			final var flex = pars
				.get(Integer.parseInt(toks[1]))
				.apply(toks[0].toLowerCase().replace('ё', 'е'));

			// !!! вывод всех неоднозначностей
			if (flex.size() > 2 && flex.get(0).source.equals(flex.get(1).source)) {
				out.println("!! Unexpected paradigm for base: "
					+ toks[0].toLowerCase().replace('ё', 'е'));
				out.println(flex.stream()
					.map(x -> x.source).collect(joining(" ")));
			}
			// !!! вывод всех неоднозначностей


			lemmas.add(flex.get(0).source);
			for (final var j : flex) {
				j.lemmaIndex = (lemmas.size() - 1);
			}
			flexions.addAll(flex);
		}
	}

	public List<List<MorphologyTag>> getAllGrammarVariants() {
		return gram.getAllVariants();
	}

	public List<String> getAllLemmas() {
		return lemmas;
	}

	public List<Flexion> getAllFlexion() {
		return flexions;
	}
}