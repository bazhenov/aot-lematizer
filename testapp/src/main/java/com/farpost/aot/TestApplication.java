package com.farpost.aot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;

public class TestApplication {

	public static void main(String[] args) throws IOException {

		var d = new HashDictionary();
		try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (!currentThread().isInterrupted()) {

				System.out.println(d.lookup(reader.readLine()));


			}
		}
	}
}
