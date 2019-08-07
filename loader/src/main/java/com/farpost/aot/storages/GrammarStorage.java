package com.farpost.aot.storages;

import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.readers.TabReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище грамматической информации
 */
public class GrammarStorage {

	private final List<List<GrammarInfo>> allVariants = new ArrayList<>();
	private final Map<String, Integer> index = new HashMap<>();

	public GrammarStorage() throws IOException {
		final var reader = new TabReader();
		for (var i = reader.readLine(); i != null; i = reader.readLine()) {
			allVariants.add(Arrays.stream(i.substring(5).split(" |,"))
				.map(GrammarInfo::fromString)
				.collect(Collectors.toList()));
			index.put(i.substring(0, 2), allVariants.size() - 1);
		}
	}

	/**
	 * @return список всех наборов грамматической информации
	 */
	public List<List<GrammarInfo>> getAllVariants() {
		return allVariants;
	}

	/**
	 * @param ancode строковый идентификатор
	 * @return индекс набора грамматики в списке наборов
	 */
	public int getVariantIndex(final String ancode) {
		return index.get(ancode);
	}


}
