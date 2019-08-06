package com.farpost.aot;

import com.farpost.aot.data.LemmaInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestApplication {

	public static void main(String[] args) throws IOException {
		final FlexionStorage flexionStorage = new FlexionStorage();
		try (final BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in)
		)) {
			while (true) {
				for (final LemmaInfo i : flexionStorage.search(reader.readLine())) {
					System.out.println(i);
				}
			}
		}
	}
}
