package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

import static com.farpost.aot.Utils.stringFromBytes;

public class CollisionFlexionsStorage {

	private final Map<String, int[]> map = new HashMap<>();

	/**
	 * принимает строку, возвращает индексы лемм и грамматики
	 */
	public int[] get(final String flexion) {
		return map.get(flexion);
	}

	public CollisionFlexionsStorage() throws IOException {
		try (DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/collisions.bin"))) {
			final int count = reader.readInt();
			for (int i = 0; i < count; ++i) {

				final byte[] strbuf = new byte[36];
				int strbufIndex = -1;
				for (byte j = reader.readByte(); j != 0; j = reader.readByte()) {
					strbuf[++strbufIndex] = j;
				}

				final String javaString = stringFromBytes(Arrays.copyOf(strbuf, strbufIndex + 1));
				final int lemmaIndex = reader.readInt();
				final int grammarIndex = reader.readInt();

				final int[] oldValue = map.get(javaString);

				if (oldValue == null) {
					map.put(javaString, new int[]{lemmaIndex, grammarIndex});
				} else {
					final int[] joinedValue = new int[oldValue.length + 2];
					System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
					joinedValue[joinedValue.length - 2] = lemmaIndex;
					joinedValue[joinedValue.length - 1] = grammarIndex;
					map.put(javaString, joinedValue);
				}
			}
		}
	}
}
