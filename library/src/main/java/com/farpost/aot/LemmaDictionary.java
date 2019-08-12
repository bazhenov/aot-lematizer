package com.farpost.aot;

import com.farpost.aot.data.FlexionInfo;
import com.farpost.aot.data.LemmaInfo;
import com.farpost.aot.storages.CollisionFlexionStorage;
import com.farpost.aot.storages.GrammarStorage;
import com.farpost.aot.storages.LemmaStorage;
import com.farpost.aot.storages.NormalFlexionStorage;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LemmaDictionary {

	private final CollisionFlexionStorage colFlex;
	private final NormalFlexionStorage norFlex;

	private final LemmaStorage lemStore;
	private final GrammarStorage gramStore;

	public LemmaDictionary() throws IOException {
		try (DataInputStream reader = new DataInputStream(
			getClass().getResourceAsStream("/MRD.BIN")
		)) {
			colFlex = new CollisionFlexionStorage(reader);
			norFlex = new NormalFlexionStorage(reader);
			lemStore = new LemmaStorage(reader);
			gramStore = new GrammarStorage(reader);
		}
	}

	private List<LemmaInfo> lookup(final int[] indexes) {
		List<LemmaInfo> res = new ArrayList<>();
		for (int i = 0; i < indexes.length; i += 2) {

			boolean existsLemma = false;

			for (final LemmaInfo info : res) {
				if (info.lemmaIndex == indexes[i]) {
					info.flexions.add(new FlexionInfo(gramStore.get(indexes[i + 1])));
					existsLemma = true;
					break;
				}
			}

			if (!existsLemma) {
				LemmaInfo info = new LemmaInfo(indexes[i], lemStore.get(indexes[i]));
				info.flexions.add(new FlexionInfo(gramStore.get(indexes[i + 1])));
				res.add(info);
			}
		}
		return res;
	}

	public List<LemmaInfo> lookup(String flexion) {
		flexion = flexion.replace('ё', 'е');

		/*final int[] nor = norFlex.get(flexion.hashCode());
		if (nor != null) {
			return lookup(nor);
		}
		final int[] col = colFlex.get(flexion);
		if (col != null) {
			return lookup(col);
		}
		return new ArrayList<>();*/

		List<LemmaInfo> defaultResult = lookup(norFlex.get(flexion.hashCode()));
		return defaultResult.isEmpty()? lookup(colFlex.get(flexion)): defaultResult;

	}
}
