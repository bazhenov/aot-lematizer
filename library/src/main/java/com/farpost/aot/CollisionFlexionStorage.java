package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.farpost.aot.Decompiler.readLine;

/**
 * Класс - хранилище служебной информации о флексиях,
 * у которых совпадает хеш, и которые по этой причине,
 * не могут быть сохранены в основеном хранилище по хешу.
 * Здесь они хранятся напрямую по строке.
 */
class CollisionFlexionStorage {

	private final Map<String, int[]> map = new HashMap<>();



	public CollisionFlexionStorage(DataInputStream reader) throws IOException {
		int count = reader.readInt();
		for (int i = 0; i < count; ++i) {

			int lemmaIndex = reader.readInt();
			int grammarIndex = reader.readInt();

			String javaString = readLine(reader);

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


	/**
	 * @param flexion строка с колизионным хешем
	 * @return индексы лемм и грамматической информации
	 */
	public int[] get(final String flexion) {
		return map.get(flexion);
	}
}
