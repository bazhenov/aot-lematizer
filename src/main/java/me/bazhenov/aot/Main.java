package me.bazhenov.aot;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.println("Loading dictionary...");
		Dictionary d = new Dictionary(new BufferedInputStream(new FileInputStream("test.dict")), new FileInputStream("tab"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.forName("utf8")));
		String line;
		while ((line = read(reader)) != null) {
			String word = line.trim();
			List<Variation> norms = d.getWordNorm(word);
			for (Variation v : norms) {
				System.out.println((v.isLemma() ? "L" : " ") + "\t" + v.getWord() + "\t" + d.getGramInfo(v.getAncode()).getDescription());
			}
		}
	}

	private static String read(BufferedReader reader) throws IOException {
		System.out.print("> ");
		return reader.readLine();
	}
}
