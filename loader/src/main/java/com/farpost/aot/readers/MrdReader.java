package com.farpost.aot.readers;

import java.io.BufferedReader;
import java.io.IOException;

import static com.farpost.aot.readers.BufferedReaderFabric.createUtf8Reader;

public class MrdReader {

	private final BufferedReader reader = createUtf8Reader("/mrd");

	public long readLong() throws IOException {
		return Long.parseLong(reader.readLine());
	}

	private void skipLines(final long count) throws IOException {
		for (long i = 0; i < count; ++i) {
			reader.readLine();
		}
	}

	public String readLine() throws IOException {
		return reader.readLine();
	}

	public void skipSection() throws IOException {
		skipLines(readLong());
	}
}
