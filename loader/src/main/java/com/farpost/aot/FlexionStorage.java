package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.readers.MrdReader;
import com.farpost.aot.storages.GrammarStorage;
import com.farpost.aot.storages.ParadigmStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Главное хранилище инкапсулирует остальные
 */
public class FlexionStorage {

	private final GrammarStorage gram = new GrammarStorage();
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
				System.out.println("!! Unexpected paradigm for base: " + toks[0].toLowerCase().replace('ё', 'е') + ":");
				for (final var j : flex) {
					System.out.print(j.source);
					System.out.print(' ');
				}
				System.out.println();
			}
			// !!! вывод всех неоднозначностей


			lemmas.add(flex.get(0).source);
			for (final var j : flex) {
				j.lemmaIndex = (lemmas.size() - 1);
			}
			flexions.addAll(flex);
		}
	}

	public List<List<GrammarInfo>> getAllGrammarVariants() {
		return gram.getAllVariants();
	}

	public List<String> getAllLemmas() {
		return lemmas;
	}

	public List<Flexion> getAllFlexion() {
		return flexions;
	}
}
