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

			int lem = intFromByteArray(block, i);
			int grm = intFromByteArray(block, i + 4);
			int hsh = intFromByteArray(block, i + 8);

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

	private static int intFromByteArray(byte[] arr, final int from) {
		return arr[from + 3] & 0xFF |
			(arr[from + 2] & 0xFF) << 8 |
			(arr[from + 1] & 0xFF) << 16 |
			(arr[from] & 0xFF) << 24;
	}

	public int[] get(final int hash) {
		return normalFlexions.get(hash);
	}
}
