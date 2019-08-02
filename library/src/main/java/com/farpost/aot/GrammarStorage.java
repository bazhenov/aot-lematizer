package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.farpost.aot.Decompiler.infoFromByte;
import static com.farpost.aot.Decompiler.isEndl;

/**
 * Хранилище наборов грамматической информации, доступных по индексу.
 */
public class GrammarStorage {

	private final GrammarInfo[][] lines;

	public GrammarStorage() throws IOException {

		try (DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/grammar.bin"))) {
			// считали количество строк
			lines = new GrammarInfo[reader.readInt()][];

			// буфер для строки
			final GrammarInfo[] buf = new GrammarInfo[12];

			for(int i = 0; i < lines.length; ++i) {

				int bufIndex = -1;

				// считываем строку
				for(byte currentByte = reader.readByte(); !isEndl(currentByte); currentByte = reader.readByte()) {
					buf[++bufIndex] = infoFromByte(currentByte);
				}

				// копируем ее в соответствующий индекс масива строк
				lines[i] = Arrays.copyOf(buf, bufIndex + 1);
			}
		}
	}

	/**
	 * @param requestedIndex индекс строки с грамматической информацией
	 * @return строка грамматической информации
	 */
	public GrammarInfo[] get(final int requestedIndex) {
		return lines[requestedIndex];
	}
}
