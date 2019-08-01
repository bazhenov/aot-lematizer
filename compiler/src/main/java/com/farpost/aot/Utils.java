package com.farpost.aot;

final public class Utils {

	public static int hashFromString(final String input) {
		final int prime = 37;
		int result = 17;
		for (final char character : input.toCharArray()) {
			result = prime * result + character;

			result += (character * 475) / 78;

		}
		return result;
	}
}
