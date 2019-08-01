package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;

public class FlexionStorage {

	private final GrammarStorage grammarStorage = new GrammarStorage();
	private final LemmaStorage lemmaStorage = new LemmaStorage();

	private final Map<Integer, int[]> flexionsData = new HashMap<>();

	public FlexionStorage() throws IOException {

		try (final DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/flexions.bin"))) {

			// магическое число всех лемм в бинарном файле
			final int flexionsDataSize =  reader.readInt();
			for (int i = 0; i < flexionsDataSize; ++i) {

				final int flexionHash = reader.readInt();
				final int indexOfLemma = reader.readInt();
				final int indexOfGrammarData = reader.readInt();

				final int[] oldValue = flexionsData.get(flexionHash);

				if (oldValue == null) {
					flexionsData.put(flexionHash, new int[]{indexOfLemma, indexOfGrammarData});
				} else {
					final int[] joinedValue = new int[oldValue.length + 2];
					System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
					joinedValue[joinedValue.length - 2] = indexOfLemma;
					joinedValue[joinedValue.length - 1] = indexOfGrammarData;
					flexionsData.put(flexionHash, joinedValue);
				}
			}
		}
	}

	/**
	 * Принимает слово, возвращает массив флексий, которыми может быть это слово.
	 */
	public Flexion[] get(final String str) {
		final int[] pointers = flexionsData.get(
			str.toLowerCase()
				.replace('ё', 'е')
				.hashCode()
		);
		if (pointers == null) {
			return new Flexion[0];
		}
		final Flexion[] results = new Flexion[pointers.length / 2];
		for (int i = 0, j = 0; i < pointers.length; i += 2, ++j) {
			results[j] = new Flexion(
				lemmaStorage.get(pointers[i]),
				grammarStorage.get(pointers[i + 1])
			);
		}

		return results;
	}
}
