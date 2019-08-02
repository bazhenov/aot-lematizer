package com.farpost.aot;

import com.farpost.aot.mrd.*;
import com.farpost.aot.tab.GrammarInfo;
import com.farpost.aot.tab.Tabfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllDataExtractor {

	private final List<List<GrammarInfo>> allInfoLines;
	private List<String> allUniqueLemmas = new ArrayList<>();
	private List<Flexion> allFlexions = new ArrayList<>();

	public AllDataExtractor() throws IOException {
		final var tabfile = new Tabfile();
		allInfoLines = tabfile.allLines;
		collectFlexionsAndLemmas(new Parser(tabfile.ancodeToLineIndex));
	}

	// собираем парадигмы
	private List<Paradigm> parseParadigms(final Reader reader, final Parser parser) throws IOException {
		final var len = reader.readLong();
		final var res = new ArrayList<Paradigm>();
		for (var i = 0; i < len; ++i) {
			res.add(parser.parseParadigm(reader.readLine()));
		}
		reader.skipSection();
		reader.skipSection();
		reader.skipSection();
		return res;
	}

	// собираем флексии
	private void collectFlexionsAndLemmas(final Parser parser) throws IOException {
		final var reader = new Reader();
		final var paradigms = parseParadigms(reader, parser);
		final var len = reader.readLong();

		final var collector = new UniqueLemmasCollector(allUniqueLemmas);
		for (var i = 0; i < len; ++i) {
			final var line = reader.readLine();
			// фильтр метапостфиксов
			if (line.startsWith("-")) {
				continue;
			}
			final var tokens = line.split(" ");

			allFlexions.addAll(
				paradigms.get(Integer.parseInt(tokens[1]))
					.apply(tokens[0], collector)
			);
		}
		reader.close();
	}

	public List<List<GrammarInfo>> getGrammarInfoLines() {
		return allInfoLines;
	}

	public List<Flexion> getFlexions() {
		return allFlexions;
	}

	public List<String> getLemmas() {
		return allUniqueLemmas;
	}
}
