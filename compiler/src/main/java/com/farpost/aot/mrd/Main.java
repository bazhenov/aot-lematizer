package com.farpost.aot.mrd;

import com.farpost.aot.AllDataExtractor;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		final var a = new AllDataExtractor();

		System.out.println(a.getLemmas());

	}
}
