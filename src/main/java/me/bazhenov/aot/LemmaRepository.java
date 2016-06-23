package me.bazhenov.aot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Обеспечивает хранение лемм и их выборку по ключу {@link Lemma#base}<br />
 * Внутренняя имплементация основывается на обычном {@link HashMap}, так что класс <b>не</b> потокобезопасный.
 */
public class LemmaRepository {

	private final Map<String, Set<Lemma>> lemmas = new HashMap<>();

	/**
	 * @param bases основы лемм
	 * @return набор лемм, у которых {@link Lemma#base} равна одному из представленных основ
	 */
	public Set<Lemma> findByBaseIn(Set<String> bases) {
		return bases.stream()
			.filter(lemmas::containsKey)
			.map(lemmas::get)
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}

	/**
	 * Пороизводит вставку указанной леммы в репозиторий. <br />
	 * При наличии данной леммы до вставки бросится исключение.
	 * @param lemma Лемма для вставки
	 */
	public void insert(Lemma lemma) {
		lemmas.putIfAbsent(lemma.getBase(), new HashSet<>());
		Set<Lemma> sameBaseLemmas = lemmas.get(lemma.getBase());
		if (sameBaseLemmas.contains(lemma)) {
			throw new IllegalArgumentException("Данная лемма уже присутствует в репозитории");
		}
		sameBaseLemmas.add(lemma);
	}

}
