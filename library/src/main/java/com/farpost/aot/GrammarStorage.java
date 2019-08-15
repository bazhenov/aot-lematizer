package com.farpost.aot;

import com.farpost.aot.data.MorphologyTag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.farpost.aot.Decompiler.*;

/**
 * Хранилище наборов грамматической информации, доступных по индексу.
 */
class GrammarStorage {

	private final MorphologyTag[][] lines;

	public GrammarStorage(DataInputStream reader) throws IOException {
		// считали количество строк
		lines = new MorphologyTag[reader.readInt()][];
		for (int i = 0; i < lines.length; ++i) {
			// считываем строку и копируем ее в соответствующий индекс масива строк
			lines[i] = readGrammarLine(reader);
		}
	}


	/**
	 * @param requestedIndex индекс строки с грамматической информацией
	 * @return строка грамматической информации
	 */
	public MorphologyTag[] get(int requestedIndex) {
		return lines[requestedIndex];
	}
}
