package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;


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


	private Lemma lookupLemma(int[] links, String word) {
		Flexion[] res = new Flexion[links.length / 2];
		for (int i = 0, j = 0; i < res.length; ++i, j += 2) {
			res[i] = new Flexion(strings[links[j]], morph[links[j + 1]]);
		}
		for (Flexion i : res) {
			if (i.getWord().equals(word)) {
				return new Lemma(res);
			}
		}
		return null;
	}

	private List<Lemma> lookupLemmas(int[] refs, String word) {
		List<Lemma> result = new ArrayList<>();
		for (int ref : refs) {
			Lemma currentLemma = lookupLemma(lemmas[ref], word);
			if (currentLemma != null) {
				result.add(currentLemma);
			}
		}
		return result;
	}

	public List<Lemma> lookup(String word) {
		word = word.toLowerCase().replace('ё', 'е');
		int[] refs = this.refs.get(word.hashCode());
		return refs == null ? emptyList() : lookupLemmas(refs, word);
	}
}
