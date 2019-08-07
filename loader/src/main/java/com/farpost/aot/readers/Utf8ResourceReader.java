package com.farpost.aot.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utf8ResourceReader {

	private final BufferedReader bufferedReader;

	public Utf8ResourceReader(final String filename) {
		bufferedReader = new BufferedReader(
			new InputStreamReader(
				getClass().getResourceAsStream(filename),
				StandardCharsets.UTF_8
			)
		);
	}

	public String readLine() throws IOException {
		return bufferedReader.readLine();
	}

	public void close() throws IOException {
		bufferedReader.close();
	}
}
