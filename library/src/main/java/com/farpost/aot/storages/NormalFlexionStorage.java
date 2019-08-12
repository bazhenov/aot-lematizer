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
		// число всех флексий в бинарном файле * 12 байт
		byte[] block = new byte[reader.readInt() * 12];
		reader.readFully(block);

		for (int i = 0; i < block.length; i += 12) {

			int lem = intFromBytes(
				block[i],
				block[i + 1],
				block[i + 2],
				block[i + 3]
			);
			int grm = intFromBytes(
				block[i + 4],
				block[i + 5],
				block[i + 6],
				block[i + 7]
			);
			int hsh = intFromBytes(
				block[i + 8],
				block[i + 9],
				block[i + 10],
				block[i + 11]
			);

			final int[] oldValue = normalFlexions.get(hsh);

			if (oldValue == null) {
				normalFlexions.put(hsh,
					new int[]{lem, grm});
			} else {
				final int[] joinedValue = new int[oldValue.length + 2];
				System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
				joinedValue[joinedValue.length - 2] = lem;
				joinedValue[joinedValue.length - 1] = grm;
				normalFlexions.put(hsh, joinedValue);
			}
		}

	}


	private static int intFromBytes(byte a, byte b, byte c, byte d) {
		return d & 0xFF |
			(c & 0xFF) << 8 |
			(b & 0xFF) << 16 |
			(a & 0xFF) << 24;
	}

	public int[] get(final int hash) {
		//return normalFlexions.get(hash);
		return normalFlexions.getOrDefault(hash, new int[0]);
	}
}
