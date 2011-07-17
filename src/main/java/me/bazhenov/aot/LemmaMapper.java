package me.bazhenov.aot;

import static java.lang.Integer.parseInt;

public class LemmaMapper implements Mapper<String, Lemma> {

	public Lemma map(String input) {
		String[] parts = input.split(" ");
		return new Lemma(parts[0].toLowerCase().replace("ё", "е"), parseInt(parts[1]));
	}
}
