package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.data.Lemma;
import com.farpost.aot.storages.CollisionFlexionStorage;
import com.farpost.aot.storages.GrammarStorage;
import com.farpost.aot.storages.LemmaStorage;
import com.farpost.aot.storages.NormalFlexionStorage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LemmaDictionary {

	private final CollisionFlexionStorage colFlex;
	private final NormalFlexionStorage norFlex;

	private final LemmaStorage lemStore;
	private final GrammarStorage gramStore;

	public LemmaDictionary() throws IOException {
		try (DataInputStream reader = new DataInputStream(getClass().getResourceAsStream("/MRD.BIN"))) {
			colFlex = new CollisionFlexionStorage(reader);
			norFlex = new NormalFlexionStorage(reader);
			lemStore = new LemmaStorage(reader);
			gramStore = new GrammarStorage(reader);
		}
	}

	private List<Lemma> lookup(final int[] indexes) {
		final Map<Integer, List<GrammarInfo[]>> res = new HashMap<>();
		for (int i = 0; i < indexes.length; i += 2) {
			res.computeIfAbsent(indexes[i], k -> new ArrayList<>())
				.add(gramStore.get(indexes[i + 1]));
		}
		return res.entrySet().stream()
			.map(pair -> new Lemma(lemStore.get(pair.getKey()), pair.getValue()))
			.collect(Collectors.toList());
	}

	public List<Lemma> lookup(final String flexion) {
		final int[] col = colFlex.get(flexion);
		if (col != null) {
			return lookup(col);
		}
		final int[] nor = norFlex.get(flexion.hashCode());
		if (nor != null) {
			return lookup(nor);
		}
		return new ArrayList<>();
	}
}
