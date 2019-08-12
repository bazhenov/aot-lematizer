package com.farpost.aot.storages;

import com.farpost.aot.data.MorphologyTag;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.farpost.aot.func.Decompiler.infoFromByte;
import static com.farpost.aot.func.Decompiler.isEndl;

/**
 * Хранилище наборов грамматической информации, доступных по индексу.
 */
public class GrammarStorage {

	private final MorphologyTag[][] lines;

	public GrammarStorage(DataInputStream reader) throws IOException {
		// считали количество строк
		lines = new MorphologyTag[reader.readInt()][];

		// буфер для строки
		final MorphologyTag[] buf = new MorphologyTag[12];

		for (int i = 0; i < lines.length; ++i) {

			int bufIndex = -1;

			// считываем строку
			for (byte currentByte = reader.readByte(); !isEndl(currentByte); currentByte = reader.readByte()) {
				buf[++bufIndex] = infoFromByte(currentByte);
			}

			// копируем ее в соответствующий индекс масива строк
			lines[i] = Arrays.copyOf(buf, bufIndex + 1);
		}
	}

	/**
	 * @param requestedIndex индекс строки с грамматической информацией
	 * @return строка грамматической информации
	 */
	public MorphologyTag[] get(final int requestedIndex) {
		return lines[requestedIndex];
	}
}
