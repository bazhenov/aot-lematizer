package com.farpost.aot;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

/**
 * Слово. Содержит свою исходную форму, плюс все флексии
 */
public class Word {
	
	private final int[] flexionsLinks;

	private List<Flexion> flexions = null;

	Word(int[] flexionsLinks) {
		this.flexionsLinks = flexionsLinks;
	}

	public List<Flexion> getFlexions() {
		if(flexions != null) {
			return flexions;
		}
		Flexion[] res = new Flexion[flexionsLinks.length / 2];
		for(int i = 0, j = 0; i < res.length; ++i, j += 2) {
			res[i] = new Flexion(flexionsLinks[j], flexionsLinks[j + 1]);
		}
		return (flexions = asList(res));
	}

	public Flexion getLemma() {
		return getFlexions().get(0);
	}

	public boolean hasFlexion(Predicate<Flexion> predicate) {
		for (Flexion i : getFlexions()) {
			if (predicate.test(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFlexionWithTag(String flexionString, MorphologyTag good) {
		return hasFlexion(flexionString, good, null);
	}

	public boolean hasFlexionWithoutTag(String flexionString, MorphologyTag bad) {
		return hasFlexion(flexionString, null, bad);
	}

	public boolean hasFlexion(String flexionString, MorphologyTag good, MorphologyTag bad) {
		return hasFlexion(f -> f.toString().equals(flexionString) && (good == null || f.has(good)) && (bad == null || f.hasNot(bad)));
	}
}
