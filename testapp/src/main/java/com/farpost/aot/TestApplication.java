package com.farpost.aot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;

public class TestApplication {

	public static void main(String[] args) throws IOException {

		/*final LemmaDictionary d = new LemmaDictionary();
		try (final BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in)
		)) {
			while (!currentThread().isInterrupted()) {
				final var res = d.lookup(reader.readLine());
				if (res.isEmpty()) {
					System.out.println(res);
					continue;
				}
				for (final LemmaInfo i : res) {
					System.out.println(i);
				}
			}
		}*/
	}
}
