package com.farpost.aot.storages;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище флексий
 */
public class NormalFlexionStorage {

	private final Map<Integer, int[]> normalFlexions = new HashMap<>();


	public int[] get(final int hash) {
		return normalFlexions.get(hash);
	}

	/**
	 * @throws IOException исключение может возникнуть при чтении словаря из ресурсов
	 */
	public NormalFlexionStorage(final DataInputStream reader) throws IOException {
		// число всех лемм в бинарном файле
		final int flexionsDataSize = reader.readInt();
		for (int i = 0; i < flexionsDataSize; ++i) {


			final int indexOfLemma = reader.readInt();
			final int indexOfGrammarData = reader.readInt();
			final int flexionHash = reader.readInt();

			final int[] oldValue = normalFlexions.get(flexionHash);

			if (oldValue == null) {
				normalFlexions.put(flexionHash,
					new int[]{indexOfLemma, indexOfGrammarData});
			} else {
				final int[] joinedValue = new int[oldValue.length + 2];
				System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
				joinedValue[joinedValue.length - 2] = indexOfLemma;
				joinedValue[joinedValue.length - 1] = indexOfGrammarData;
				normalFlexions.put(flexionHash, joinedValue);
			}
		}
	}
}
