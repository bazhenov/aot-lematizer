package me.bazhenov.aot;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toSet;

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
			.map(base -> lemmas.getOrDefault(firstChar(base), emptyMap()).get(base))
			.filter(i -> i != null)
			.flatMap(Collection::stream)
			.collect(toSet());
	}

	/**
	 * Пороизводит вставку указанной леммы в репозиторий. При наличии данной леммы до вставки бросится исключение.
	 *
	 * @param lemma лемма для вставки
	 */
	public void insert(Lemma lemma) {
		String base = lemma.getBase();
		Set<Lemma> sameBaseLemmas = lemmas.computeIfAbsent(firstChar(base), c -> new HashMap<>())
			.computeIfAbsent(base, b -> new HashSet<>());

		checkState(sameBaseLemmas.add(lemma), "Lemma already in repository");
	}

	public void clear() {
		lemmas.clear();
	}
}
