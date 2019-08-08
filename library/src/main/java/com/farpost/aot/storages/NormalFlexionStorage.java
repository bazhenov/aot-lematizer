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

	/**
	 * @throws IOException исключение может возникнуть при чтении словаря из ресурсов
	 */
	public NormalFlexionStorage(DataInputStream reader) throws IOException {
		// число всех лемм в бинарном файле
		final int flexionsDataSize = reader.readInt();

		final byte[] block = new byte[flexionsDataSize * 12];

		reader.readFully(block);

		for (int i = 0; i < block.length; i += 12) {

			int lemmIndex = intFromBytes(block[i], block[i + 1], block[i + 2], block[i + 3]);
			int gramIndex = intFromBytes(block[i + 4], block[i + 5], block[i + 6], block[i + 7]);
			int hash = intFromBytes(block[i + 8], block[i + 9], block[i + 10], block[i + 11]);

			final int[] oldValue = normalFlexions.get(hash);

			if (oldValue == null) {
				normalFlexions.put(hash,
					new int[]{lemmIndex, gramIndex});
			} else {
				final int[] joinedValue = new int[oldValue.length + 2];
				System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
				joinedValue[joinedValue.length - 2] = lemmIndex;
				joinedValue[joinedValue.length - 1] = gramIndex;
				normalFlexions.put(hash, joinedValue);
			}
		}

	}

	private static int intFromBytes(byte a, byte b, byte c, byte d) {
		return 0;
	}

	public int[] get(final int hash) {
		return normalFlexions.get(hash);
	}
}
