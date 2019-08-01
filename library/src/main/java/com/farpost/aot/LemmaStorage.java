package com.farpost.aot;

import me.bazhenov.aot.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.farpost.aot.Utils.stringFromBytes;

public class LemmaStorage {

	private final byte[][] strings = new byte[171365][];

	// это поле нужно методу addBinaryString
	private int currentItem = -1;

	public LemmaStorage() throws IOException {
		try (final InputStream lemmasReader = getClass().getResourceAsStream("/lemmas.bin")) {
			final byte[] buf = new byte[36];
			int bufIndex = -1;
			final byte endl = 95;
			while (true) {
				final byte currentByte = (byte) lemmasReader.read();
				if (currentByte == 0) {
					break;
				}
				if (currentByte == endl) {
					addBinaryString(Arrays.copyOf(buf, bufIndex + 1));
					bufIndex = -1;
					continue;
				}
				buf[++bufIndex] = currentByte;
			}
		}
	}

	private void addBinaryString(final byte[] str) {
		strings[++currentItem] = str;
	}

	/**
	 * Принимает индекс леммы, возвращает лемму
 	 */
	public String get(final int requestIndex) {
		return stringFromBytes(strings[requestIndex]);
	}
}
