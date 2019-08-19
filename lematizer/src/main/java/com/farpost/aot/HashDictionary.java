package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;


public class HashDictionary {

	private final MorphologyTag[][] morph;
	private final String[] strings;
	private final int[][] lemmas;
	private final Map<Integer, int[]> refs;


	public HashDictionary() throws IOException {
		try (DataInputStream file = new DataInputStream(getClass().getResourceAsStream("/MRD.BIN"))) {
			morph = Reader.readMorph(ByteBlock.readBlockFrom(file));
			strings = Reader.readStrings(ByteBlock.readBlockFrom(file));
			lemmas = Reader.readLemmas(ByteBlock.readBlockFrom(file));
			refs = Reader.readRefs(ByteBlock.readBlockFrom(file));
		}
	}


	public List<List<Flexion>> lookup(String word) {
		word = word.toLowerCase().replace('ё', 'е');
		int[] refs = this.refs.get(word.hashCode());
		if (refs == null) {
			return Collections.emptyList();
		}
		List<List<Flexion>> res = new ArrayList<>();
		for (int i = 0; i < refs.length; ++i) {
			int[] encodedLemma = lemmas[refs[i]];
			Flexion[] normalLemma = new Flexion[encodedLemma.length / 2];
			boolean equalityWithWord = false;
			for (int j = 0, k = 0; j < encodedLemma.length; j += 2, ++k) {
				normalLemma[k] = new Flexion(strings[encodedLemma[j]], morph[encodedLemma[j + 1]]);
				if (equalityWithWord) {
					continue;
				}
				if (normalLemma[k].getString().equals(word)) {
					equalityWithWord = true;
				}
			}
			if (equalityWithWord) {
				res.add(asList(normalLemma));
			}

		}
		return res;
	}
}
