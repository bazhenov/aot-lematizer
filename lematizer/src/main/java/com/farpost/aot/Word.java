package com.farpost.aot;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * Слово. Содержит свою исходную форму, плюс все флексии
 */
public class Word {

	private final Flexion[] flexions;

	Word(int[] flexionsLinks) {
		this.flexions = new Flexion[flexionsLinks.length / 2];
		for (int i = 0, j = 0; i < flexions.length; ++i, j += 2) {
			flexions[i] = new Flexion(flexionsLinks[j], flexionsLinks[j + 1]);
		}
	}

	public List<Flexion> getFlexions() {
		return asList(flexions);
	}

	public Flexion getLemma() {
		return flexions[0];
	}

	public boolean hasFlexionWith(Predicate<Flexion> predicate) {
		for (Flexion i : flexions) {
			if (predicate.test(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFlexionWithTag(String flexionString, MorphologyTag good) {
		return hasFlexionWith(flexionString, good, null);
	}

	public boolean hasFlexionWithoutTag(String flexionString, MorphologyTag bad) {
		return hasFlexionWith(flexionString, null, bad);
	}

	public boolean hasFlexionWith(String flexionString, MorphologyTag contained, MorphologyTag notContained) {
		return hasFlexionWith(
			f ->
				f.toString().equals(flexionString)
					&& (contained == null || f.has(contained))
					&& (notContained == null || f.hasNot(notContained)));
	}
}
