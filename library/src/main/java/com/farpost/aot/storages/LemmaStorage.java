package com.farpost.aot.storages;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.farpost.aot.func.Decompiler.readLine;

/**
 * Хранилище лемм, доступных по индексу
 */
public class LemmaStorage {

	private final String[] lines;

	public LemmaStorage(DataInputStream reader) throws IOException {
		lines = new String[reader.readInt()];
		for (int i = 0; i < lines.length; ++i) {
			lines[i] = readLine(reader);
		}
	}

	/**
	 * Принимает индекс леммы, возвращает лемму
	 *
	 * @param requestedIndex индекс леммы
	 * @return лемма
	 */
	public String get(final int requestedIndex) {
		return lines[requestedIndex];
	}
}
