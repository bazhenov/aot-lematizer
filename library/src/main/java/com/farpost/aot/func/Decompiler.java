package com.farpost.aot.func;

import com.farpost.aot.data.GrammarInfo;

import static me.bazhenov.aot.Utils.byteToChar;

/**
 * Класс хранит общую для всех информацию о преобразованиях из байтов в объекты
 */
public final class Decompiler {

	// оптимизация чтобы не выделять каждый раз память под буфер юникодных символов
	private static final char[] resultBuffer = new char[36];

	/**
	 * Конвертирует массив байт в строку.
	 * @param bytes байты русской кодировки
	 * @return юникодная строка
	 */
	public static String stringFromBytes(final byte[] bytes) {
		for(int i = 0; i < bytes.length; ++i) {
			resultBuffer[i] = byteToChar(bytes[i]);
		}
		return String.valueOf(resultBuffer, 0, bytes.length);
	}

	/**
	 * Преобразует один байт в экземпляер перечисления с грамматической информацией.
	 * Для преобразования, используется порядковый номер равный этому байту, в списке экземпляов перечисления.
	 * Поэтому байт может быть равным нулю.
	 * @param i байт
	 * @return экземпляр перечисления с грамматической информацией
	 */
	public static GrammarInfo infoFromByte(final byte i) {
		return GrammarInfo.values()[i];
	}

	/**
	 * Метод проверяет, является ли этот байт маркером конца строки
	 * @param b байт
	 * @return true, если это маркер конца строки, false, если это байт данных
	 */
	public static boolean isEndl(final byte b) {
		// Почему 95?
		// Так как хотелось иметь общий для разных бинарников маркер переноса, был выбран этот номер,
		// потому что не является отрицательным (отрицательные зарезрвированы под буквы),
		// и при этом он больше любого порядкого номера GrammarInfo (которые зарезервированы под хранение GrammarInfo),
		// начиная счет с нуля.
		return b == 95;
	}
}
