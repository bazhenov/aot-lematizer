package com.farpost.aot.func;

import com.farpost.aot.data.MorphologyTag;
import me.bazhenov.aot.Utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Класс хранит общую для всех информацию о преобразованиях из байтов в объекты
 */
public final class Decompiler {

	/**
	 * оптимизация чтобы не выделять каждый раз память под буфер юникодных символов
	 * 36 - длина наибольшей флексии, поэтому больше нам и не потребуется
	 */
	private static final char[] strBuf = new char[36];

	/**
	 * Читает из потока байт юникодную строку
	 * @param reader поток байт
	 * @return строка
	 * @throws IOException
	 */
	public static synchronized String readLine(DataInputStream reader) throws IOException {
		int index = -1;
		for (byte j = reader.readByte(); isContent(j); j = reader.readByte()) {
			strBuf[++index] = Utils.byteToChar(j);
		}
		return String.valueOf(strBuf, 0, index + 1);
	}

	/**
	 * Аналогичная оптимизация для грамматики
	 */
	private static final MorphologyTag[] grmBuf = new MorphologyTag[12];

	/**
	 * Читает из потока байт набор грамматики
	 * @param reader поток байт
	 * @return набор грамматики
	 * @throws IOException
	 */
	public static synchronized MorphologyTag[] readGrammarLine(DataInputStream reader) throws IOException {
		int bufIndex = -1;
		// считываем строку
		for (byte currentByte = reader.readByte(); isContent(currentByte); currentByte = reader.readByte()) {
			grmBuf[++bufIndex] =  MorphologyTag.values()[currentByte];
		}
		return Arrays.copyOf(grmBuf, bufIndex + 1);
	}



	/**
	 * Метод проверяет, является ли этот байт маркером конца строки
	 * @param b байт
	 * @return false, если это маркер конца строки, true, если это байт данных
	 */
	private static boolean isContent(final byte b) {
		// Почему 100?
		// Так как хотелось иметь общий для разных бинарников маркер переноса, был выбран этот номер,
		// потому что не является отрицательным (отрицательные зарезрвированы под буквы),
		// и при этом он больше любого порядкого номера GrammarInfo (которые зарезервированы под хранение GrammarInfo),
		// начиная счет с нуля.
		return b != 100;
	}
}
