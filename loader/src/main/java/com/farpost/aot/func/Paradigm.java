package com.farpost.aot.func;

import com.farpost.aot.data.Flexion;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Paradigm implements Function<String, List<Flexion>> {

	private final Collection<Mod> mods;

	public Paradigm(Collection<Mod> mods) {
		this.mods = mods;
	}

	public List<Flexion> apply(final String base) {
		return mods.stream().map(m -> m.apply(base)).collect(Collectors.toList());
	}
}
