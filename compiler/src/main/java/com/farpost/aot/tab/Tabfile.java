package com.farpost.aot.tab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.farpost.aot.tab.Parser.parseAncode;
import static com.farpost.aot.tab.Parser.parseInfo;

public class Tabfile {
	public final List<List<GrammarInfo>> allLines = new ArrayList<>();
	public final Map<String, Integer> ancodeToLineIndex = new HashMap<>();

	public Tabfile() throws IOException {
		final var reader = new Reader();
		for (var line = reader.readLine(); line != null; line = reader.readLine()) {


			allLines.add(parseInfo(line));
			ancodeToLineIndex.put(parseAncode(line), allLines.size() - 1);

		}
		reader.close();
	}
}
