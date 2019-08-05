package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.func.Hash;
import com.farpost.aot.storages.CollisionFlexionStorage;
import com.farpost.aot.storages.CollisionHashStorage;
import com.farpost.aot.storages.GrammarStorage;
import com.farpost.aot.storages.LemmaStorage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Этот класс не простое хранилище, а объединяющее звено для всех остальных.
 * Основной класс для работы клиентских приложений.
 */
public class FlexionStorage {

	// список хранилищ которые объединяются здесь
	private final GrammarStorage grammarStorage = new GrammarStorage();
	private final LemmaStorage lemmaStorage = new LemmaStorage();
	private final CollisionHashStorage collisionHashStorage = new CollisionHashStorage();
	private final CollisionFlexionStorage collisionFlexionStorage = new CollisionFlexionStorage();
	// данные непосредственно этого хранилища
	private final Map<Integer, int[]> flexionStorageData = new HashMap<>();

	/**
	 * Начало работы с бибилиотекой, должно начинаться с создания этого объекта
	 * @throws IOException исключение может возникнуть при чтении словаря из ресурсов.
	 */
	public FlexionStorage() throws IOException {
		try (final DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/flexions.bin"))) {
			// число всех лемм в бинарном файле
			final int flexionsDataSize = reader.readInt();
			for (int i = 0; i < flexionsDataSize; ++i) {

				final int flexionHash = reader.readInt();
				final int indexOfLemma = reader.readInt();
				final int indexOfGrammarData = reader.readInt();

				final int[] oldValue = flexionStorageData.get(flexionHash);

				if (oldValue == null) {
					flexionStorageData.put(flexionHash, new int[]{indexOfLemma, indexOfGrammarData});
				} else {
					final int[] joinedValue = new int[oldValue.length + 2];
					System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
					joinedValue[joinedValue.length - 2] = indexOfLemma;
					joinedValue[joinedValue.length - 1] = indexOfGrammarData;
					flexionStorageData.put(flexionHash, joinedValue);
				}
			}
		}
	}

	/**
	 * Принимает индексы лемм и грамматики, возвращает набор флексий
	 * Используется массив, так как число флексий заранее известно
	 *
	 * @param index индексы по порядку - лемма, грамматика, лемма, граматика, и т. д.
	 * @return набор флексий
	 */
	private Flexion[] get(final int[] index) {
		final Flexion[] results = new Flexion[index.length / 2];
		for (int i = 0, j = 0; i < index.length; i += 2, ++j) {
			results[j] = new Flexion(
				lemmaStorage.get(index[i]),
				grammarStorage.get(index[i + 1])
			);
		}
		return results;
	}

	/**
	 * Это центральный метод всей библиотеки.
	 * Принимает строку, и если она является словоформой неких известных нам лемм,
	 * то возвращет все эти леммы + набор грамматической информации для каждого случая.
	 * @param str флексия
	 * @return набор информации лемма + грамматика
	 */
	public Collection<Flexion> get(final String str) {
		// получаем хеш по формуле использовавшейся при компиляции словаря
		final int trueHash = Hash.fromString(
			str.toLowerCase().replace('ё', 'е')
		);
		// если хеш колизионный
		return Arrays.asList(get(collisionHashStorage.containsHash(trueHash) ?
			// то получаем соответсвующие индексы напрямую по строке во избежание колиззий
			collisionFlexionStorage.get(str) :
			// если хеш нормальный, то получаем индексы по хешу
			flexionStorageData.getOrDefault(trueHash, new int[0])
		));
	}
}
