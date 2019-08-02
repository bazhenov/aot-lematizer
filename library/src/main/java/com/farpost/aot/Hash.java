package com.farpost.aot;

/**
 * Статичный класс, а по сути, часть имени функции
 */
final public class Hash {

	/**
	 * Эта функция используется для расчета хеша строки.
	 * Нужно использовать именно её,
	 * так как она порождает намного меньше коллизий чем String.hashCode
	 * @param input строка
	 * @return хеш
	 */
	public static int fromString(final String input) {
		final int prime = 37;
		int result = 17;
		for (final char character : input.toCharArray()) {
			result = prime * result + character;
			result += (character * 475) / 78;
		}
		return result;
	}
}
