package com.farpost.aot;

import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * Класс сжимает набор лемм
 */
public class Zip {

	public static ZipResult zip(List<List<Flexion>> lemmas) {
		var index = new UniqueValues();
		var miniLemmas = lemmas.stream().map(lemma -> zip(lemma, index)).collect(toUnmodifiableList());
		return new ZipResult(miniLemmas, index.strings, index.tags);
	}

	private static List<MiniFlexion> zip(List<Flexion> lemma, UniqueValues index) {
		return lemma.stream()
			.map(f -> new MiniFlexion(
				index.indexOf(f.getWord()),
				index.indexOf(new HashSet<>(f.getTags()))
				)
			)
			.collect(toUnmodifiableList());
	}

	/**
	 * Уникальные наборы значений
	 */
	private static class UniqueValues {

		private final Map<Set<MorphologyTag>, Integer> g_opt = new HashMap<>();
		private final Map<String, Integer> s_opt = new HashMap<>();

		final List<String> strings = new ArrayList<>();
		final List<Set<MorphologyTag>> tags = new ArrayList<>();

		/**
		 * @param newTag морфологические теги
		 * @return индекс тегов в тегохранилище
		 */
		int indexOf(Set<MorphologyTag> newTag) {
			var opt = g_opt.get(newTag);
			if (opt == null) {
				tags.add(newTag);
				g_opt.put(newTag, tags.size() - 1);
				return tags.size() - 1;
			}
			return opt;
		}

		/**
		 * @param str строка
		 * @return индекс строки в строкохранилище
		 */
		int indexOf(String str) {
			var opt = s_opt.get(str);
			if (opt == null) {
				strings.add(str);
				s_opt.put(str, strings.size() - 1);
				return strings.size() - 1;
			}
			return opt;
		}
	}
}
