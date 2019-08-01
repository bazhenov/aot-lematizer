package com.farpost.aot.mrd;

import com.farpost.aot.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Paradigm {

	public final List<Mod> mods;

	public List<Flexion> apply(final String s, final UniqueLemmasCollector collector) {
		final var res = mods.stream().map(m -> m.apply(s)).collect(Collectors.toList());
		final int lemmaIndex = collector.getIndexOfLemma(res.get(0).flexion);
		return res.stream()
			.map(x -> new Flexion(Utils.hashFromString(x.flexion), lemmaIndex, x.infoLineIndex, x.flexion))
			.collect(Collectors.toList());
	}

	public Paradigm(final List<Mod> mods) {
		this.mods = mods;
	}
}
