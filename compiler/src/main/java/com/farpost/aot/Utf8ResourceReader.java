package com.farpost.aot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public abstract class Utf8ResourceReader {

	protected final BufferedReader bufReader;

	public Utf8ResourceReader(final String filename) throws UnsupportedEncodingException {
		bufReader = new BufferedReader(
			new InputStreamReader(
				getClass().getResourceAsStream(filename),
				StandardCharsets.UTF_8
			));
	}

	public void close() throws IOException {
		bufReader.close();
	}
}
