package me.bazhenov.aot;

import java.util.Comparator;

public class VariationComparator implements Comparator<Variation> {

	public int compare(Variation o1, Variation o2) {
		return o1.getWord().compareTo(o2.getWord());
	}
}
