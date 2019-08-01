package com.farpost.aot.tab;

import com.farpost.aot.Utf8ResourceReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Reader extends Utf8ResourceReader {

	public Reader() throws UnsupportedEncodingException {
		super("/tab");
	}

	public String readLine() throws IOException {
		final var line = bufReader.readLine();
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
