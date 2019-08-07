package com.farpost.aot.readers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TabReader extends Utf8ResourceReader {

	public TabReader() throws UnsupportedEncodingException {
		super("/tab");
	}

	@Override
	public String readLine() throws IOException {
		final var line = super.readLine();
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
