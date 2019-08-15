package com.farpost.aot;

import java.io.IOException;

public final class App {

	public static void main(String[] args) throws IOException {
		long bytes = 0;
		for (var i : LemmasReader.readLemmas()) {
			for (var j : i) {
				bytes += 2;
			}
			bytes += 1;
		}
		System.out.println(bytes);
	}
}
