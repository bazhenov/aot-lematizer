package storages;

import data.GrammarInfo;
import readers.TabReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabStorage {

	private final List<List<GrammarInfo>> lines = new ArrayList<>();
	private final Map<String, Integer> indexes = new HashMap<>();

	public List<List<GrammarInfo>> getAllLines() {
		return lines;
	}

	public int getLineIndex(final String ancode) {
		return indexes.get(ancode);
	}

	public TabStorage() throws IOException {
		final var reader = new TabReader();
		for (var i = reader.readLine(); i != null; i = reader.readLine()) {

		}
		reader.close();
	}
}
