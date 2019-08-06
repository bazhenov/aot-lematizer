package storages;

import data.GrammarInfo;
import readers.TabReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище грамматической информации
 */
public class TabStorage {

	private final List<List<GrammarInfo>> allVariants = new ArrayList<>();
	private final Map<String, Integer> index = new HashMap<>();

	/**
	 * Возвращает список всех вариантов
	 *
	 * @return
	 */
	public List<List<GrammarInfo>> getAllVariants() {
		return allVariants;
	}

	/**
	 * @param ancode строковый идентификатор
	 * @return индекс варианта в списке вариантов
	 */
	public int getVariantIndex(final String ancode) {
		return index.get(ancode);
	}

	public TabStorage() throws IOException {
		final var reader = new TabReader();
		for (var i = reader.readLine(); i != null; i = reader.readLine()) {

		}
		reader.close();
	}
}
