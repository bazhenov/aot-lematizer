package com.farpost.aot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;

public class TestApplication {

	public static void main(String[] args) throws IOException {

		var dict = new HashDictionary();
		try (var console = new BufferedReader(new InputStreamReader(System.in))) {
			while (!currentThread().isInterrupted()) {

				var res = dict.lookup(console.readLine());
				if (res.isEmpty()) {
					System.out.println("[]");
					continue;
				}

				for (int counter = 1; counter <= res.size(); ++counter) {
					var lemma = res.get(counter - 1).getFlexions();
					System.out.println(counter + ". " + lemma.get(0));
					for (var flex : lemma) {
						System.out.println("     " + flex);
					}
				}


			}
		}
	}
}
