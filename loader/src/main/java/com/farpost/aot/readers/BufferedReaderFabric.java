package com.farpost.aot.readers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BufferedReaderFabric {

	public static BufferedReader createUtf8Reader(String filename) {
		return new BufferedReader(
			new InputStreamReader(
				BufferedReaderFabric.class.getResourceAsStream(filename),
				StandardCharsets.UTF_8
			)
		);
	}
}
