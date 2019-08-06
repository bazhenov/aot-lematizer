package com.farpost.aot;

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
				System.out.println(flexionStorage.search(reader.readLine()));
			}
		}
	}
}
