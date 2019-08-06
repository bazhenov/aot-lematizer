package com.farpost.aot;

import com.farpost.aot.data.FlexionInfo;
import com.farpost.aot.data.LemmaInfo;
import com.farpost.aot.func.Hash;
import com.farpost.aot.storages.CollisionFlexionStorage;
import com.farpost.aot.storages.CollisionHashStorage;
import com.farpost.aot.storages.GrammarStorage;
import com.farpost.aot.storages.LemmaStorage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	 * Начало работы с библиотекой, должно начинаться с создания этого объекта
	 *
	 * @throws IOException исключение может возникнуть при чтении словаря из ресурсов
	 */
	public FlexionStorage() throws IOException {
		try (final DataInputStream reader = new DataInputStream(
			getClass().getResourceAsStream("/flexions.bin")
		)) {
			// число всех лемм в бинарном файле
			final int flexionsDataSize = reader.readInt();
			for (int i = 0; i < flexionsDataSize; ++i) {

				final int flexionHash = reader.readInt();
				final int indexOfLemma = reader.readInt();
				final int indexOfGrammarData = reader.readInt();

				final int[] oldValue = flexionStorageData.get(flexionHash);

				if (oldValue == null) {
					flexionStorageData.put(flexionHash,
						new int[]{indexOfLemma, indexOfGrammarData});
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
	 * Принимает индексы cтрок лемм и грамматики, возвращает набор уникальных лемм
	 *
	 * @param index индексы по порядку - строка леммы, грамматика, строка леммы, граматика, и т. д.
	 * @return набор лемм
	 */
	private List<LemmaInfo> search(final int[] index) {

		final Map<Integer, List<FlexionInfo>>
			lemmaIndexToFlexionsIndexes = new HashMap<>();

		for (int i = 0, j = 0; i < index.length; i += 2, ++j) {

			final int indexOfLemma = index[i];
			final int indexOfGrammar = index[i + 1];

			lemmaIndexToFlexionsIndexes
				.computeIfAbsent(indexOfLemma, k -> new ArrayList<>())
				.add(new FlexionInfo(grammarStorage.get(indexOfGrammar)));
		}

		return lemmaIndexToFlexionsIndexes.entrySet().stream()
			.map(i ->
				new LemmaInfo(lemmaStorage.get(i.getKey()), i.getValue())
			)
			.collect(Collectors.toList());
	}


	/**
	 * Основной метод работы с библиотекой
	 *
	 * @param str строка флексии
	 * @return набор уникальных лемм,
	 * к каждой из которых прикреплена информация обо всех её флексиях,
	 * совпавших (!) с изначально запрошенной.
	 */
	public List<LemmaInfo> search(final String str) {
		// получаем хеш по формуле использовавшейся при компиляции словаря
		final int trueHash = Hash.fromString(
			str.toLowerCase().replace('ё', 'е')
		);
		// если хеш колизионный
		return search(collisionHashStorage.containsHash(trueHash) ?
			// то получаем соответствующие индексы напрямую по строке во избежание колиззий
			collisionFlexionStorage.get(str) :
			// если хеш нормальный, то получаем индексы по хешу
			flexionStorageData.getOrDefault(trueHash, new int[0])
		);
	}


	/**
	 * Основной метод работы с библиотекой
	 *
	 * @param str строка флексии
	 * @return набор лемм,
	 * которые (!) могут совпадать по написанию, но не по смыслу,
	 * к каждой из которых прикреплена информация обо всех её флексиях,
	 * совпавших (!) с изначально запрошенной.
	 */
	/*public List<LemmaInfo> searchLemmas(final String str) {
		throw new UnsupportedOperationException();
	}*/
}
