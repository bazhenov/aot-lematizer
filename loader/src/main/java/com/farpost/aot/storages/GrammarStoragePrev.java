package com.farpost.aot.storages;

import com.farpost.aot.data.MorphologyTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.farpost.aot.readers.BufferedReaderFabric.createUtf8Reader;

/**
 * Хранилище грамматической информации
 */
public class GrammarStoragePrev {

	private final List<List<MorphologyTag>> allVariants = new ArrayList<>();
	private final Map<String, Integer> index = new HashMap<>();

	public GrammarStoragePrev() throws IOException {
		BufferedReader reader = createUtf8Reader("/tab");
		for (var i = readLine(reader); i != null; i = readLine(reader)) {
			allVariants.add(Arrays.stream(i.substring(5).split(" |,"))
				.map(MorphologyTag::fromString)
				.collect(Collectors.toList()));
			index.put(i.substring(0, 2), allVariants.size() - 1);
		}
	}

	private static String readLine(BufferedReader reader) throws IOException {
		final var line = reader.readLine();
		if (line == null) {
			return null;
		}
		// отбрасываем комментарии и пустые строки
		if (line.isEmpty() || line.startsWith("//")) {
			return readLine(reader);
		}
		return line;
	}

	/**
	 * @return список всех наборов грамматической информации
	 */
	public List<List<MorphologyTag>> getAllVariants() {
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
