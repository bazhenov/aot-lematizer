package com.farpost.aot;

import java.io.IOException;

public class TestApplication {

	public static void main(String[] args) throws IOException {

		new LemmaDictionary();

		/*final LemmaDictionary d = new LemmaDictionary();
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
		}*/
	}
}
