package com.farpost.aot.storages;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Хранилище коллизионных хешей.
 */
public class CollisionHashStorage {
	private final Set<Integer> hashes = new HashSet<>();

	/**
	 * @param hash хеш рассчитанный функцией (!) Hash.fromString
	 * @return входит ли хеш в список колизионных
	 */
	public boolean containsHash(final int hash) {
		return hashes.contains(hash);
	}

	public CollisionHashStorage() throws IOException {
		try (DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/hashes.bin"))) {
			final int count = reader.readInt();
			for (int i = 0; i < count; ++i) {
				hashes.add(reader.readInt());
			}
		}
	}
}
