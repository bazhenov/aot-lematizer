package com.farpost.aot;

import com.farpost.aot.data.LemmaInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestApplication {

	public static void main(String[] args) throws IOException {

		final LemmaDictionary d = new LemmaDictionary();
		try (final BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in)
		)) {
			while (true) {
				final var res = d.lookup(reader.readLine());
				if (res.isEmpty()) {
					System.out.println(res);
					continue;
				}
				for (final LemmaInfo i : res) {
					System.out.println(i);
				}
			}
		}
	}
}
