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

	// оптимизация чтобы не выделять каждый раз память под буффер юникодных символов
	private static final char[] resultBuffer = new char[36];

	public static String stringFromBytes(final byte[] bytes) {
		int resIndex = -1;
		for (final byte b : bytes) {
			resultBuffer[++resIndex] = me.bazhenov.aot.Utils.byteToChar(b);
		}
		return String.valueOf(resultBuffer, 0, resIndex + 1);
	}
}
