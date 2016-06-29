package me.bazhenov.aot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Обеспечивает хранение лемм и их выборку по ключу {@link Lemma#base}<br />
 * Внутренняя имплементация основывается на обычном {@link HashMap}, так что класс <b>не</b> потокобезопасный.
 */
public class LemmaRepository {

	/**
	 * Наборы лемм, сгруппированые по первой букве основания
	 */
	private final Map<Character, Map<String, Set<Lemma>>> lemmas = new HashMap<>();

	public static char firstChar(String s) {
		return s.isEmpty() ? Character.MIN_VALUE : s.charAt(0);
	}

	/**
	 * @param bases основы лемм
	 * @return набор лемм, у которых {@link Lemma#base} равна одному из представленных основ
	 */
	public Set<Lemma> findByBaseIn(Set<String> bases) {
		return bases.stream()
			.filter(base -> lemmas.getOrDefault(firstChar(base), new HashMap<>()).containsKey(base))
			.map(base -> lemmas.get(firstChar(base)).get(base))
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}

	/**
	 * Пороизводит вставку указанной леммы в репозиторий. <br />
	 * При наличии данной леммы до вставки бросится исключение.
	 * @param lemma Лемма для вставки
	 */
	public void insert(Lemma lemma) {
		String base = lemma.getBase();
		lemmas.putIfAbsent(firstChar(base), new HashMap<>());
		lemmas.get(firstChar(base)).putIfAbsent(base, new HashSet<>());
		Set<Lemma> sameBaseLemmas = lemmas.get(firstChar(base)).get(base);
		if (sameBaseLemmas.contains(lemma)) {
			throw new IllegalArgumentException("Данная лемма уже присутствует в репозитории");
		}
		sameBaseLemmas.add(lemma);
	}

	public void clear() {
		lemmas.clear();
	}
}
