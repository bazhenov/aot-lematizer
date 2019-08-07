package com.farpost.aot.readers;

import java.io.BufferedReader;
import java.io.IOException;

import static com.farpost.aot.readers.BufferedReaderFabric.createUtf8Reader;

public class TabReader {

	private BufferedReader reader = createUtf8Reader("/tab");

	public String readLine() throws IOException {
		final var line = reader.readLine();
		if (line == null) {
			return null;
		}
		// отбрасываем комментарии и пустые строки
		if (line.isEmpty() || line.startsWith("//")) {
			return readLine();
		}
		return line;
	}
}
