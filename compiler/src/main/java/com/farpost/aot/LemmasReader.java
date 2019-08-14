package com.farpost.aot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.toList;

final class LemmasReader {

	private static BufferedReader bufferedReaderOfResource(String resourceName) {
		return new BufferedReader(
			new InputStreamReader(
				LemmasReader.class.getResourceAsStream(resourceName),
				StandardCharsets.UTF_8
			)
		);
	}

	private static String readGramtabLine(BufferedReader reader) throws IOException {
		var line = reader.readLine();
		if (line == null) {
			return null;
		}
		// отбрасываем комментарии и пустые строки
		if (line.isEmpty() || line.startsWith("//")) {
			return readGramtabLine(reader);
		}
		return line;
	}

	private static Map<String, MorphologyTag[]> readMorphology() throws IOException {
		var result = new HashMap<String, MorphologyTag[]>();
		try (var reader = bufferedReaderOfResource("/tab")) {
			for (var line = readGramtabLine(reader); line != null; line = readGramtabLine(reader)) {
				result.put(
					line.substring(0, 2),
					Arrays.stream(line.substring(5).split("[ ,]"))
						.map(MorphologyTag::fromString).toArray(MorphologyTag[]::new)
				);
			}
		}
		return result;
	}

	private static int readInt(BufferedReader reader) throws IOException {
		return Integer.parseInt(reader.readLine());
	}

	private  static void skipLines(BufferedReader reader, int count) throws IOException {
		for (var i = 0; i < count; ++i) {
			reader.readLine();
		}
	}

	private static void skipSection(BufferedReader reader) throws IOException {
		skipLines(reader, readInt(reader));
	}

	private static List<String> readParadigmsSection(BufferedReader reader) throws IOException {
		var result = new ArrayList<String>();
		var len = readInt(reader);
		for(var i = 0; i < len; ++i) {
			result.add(reader.readLine());
		}
		return result;
	}

	public static Collection<Flexion[]> readLemmas() throws IOException {
		var result = new ArrayList<Flexion[]>();
		var morphMap = readMorphology();
		try(var reader = bufferedReaderOfResource("/mrd")) {
			var paradigms = readParadigmsSection(reader);
			skipSection(reader);
			skipSection(reader);
			skipSection(reader);
			var len = readInt(reader);
			for(var i = 0; i < len; ++i) {
				var line = reader.readLine();
				// фильтр метапостфиксов
				if (line.startsWith("-")) {
					continue;
				}
				var tokens = line.split(" ");
				result.add(Flexion.from(tokens[0], paradigms.get(Integer.parseInt(tokens[1])), morphMap));
			}
		}
		return result;
	}
}
