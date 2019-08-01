package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CollisionHashesStorage {
	private final Set<Integer> hashes = new HashSet<>();

	public boolean containsHash(final int hash) {
		return hashes.contains(hash);
	}

	public CollisionHashesStorage() throws IOException {
		try (DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/hashes.bin"))) {
			final int count = reader.readInt();
			for (int i = 0; i < count; ++i) {
				hashes.add(reader.readInt());
			}
		}
	}
}
