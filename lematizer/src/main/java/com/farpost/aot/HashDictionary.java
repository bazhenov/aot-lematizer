package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;


public class HashDictionary {

	final MorphologyTag[][] allMorphologyTags;
	final String[] allFlexionStrings;

	private final int[][] lemmas;
	private final Map<Integer, int[]> refs;

	public HashDictionary() throws IOException {
		try (DataInputStream file = new DataInputStream(getClass().getResourceAsStream("/MRD.BIN"))) {
			allMorphologyTags = Reader.readMorph(ByteBlock.readBlockFrom(file));
			allFlexionStrings = Reader.readStrings(ByteBlock.readBlockFrom(file));
			lemmas = Reader.readLemmas(ByteBlock.readBlockFrom(file));
			refs = Reader.readRefs(ByteBlock.readBlockFrom(file));
		}
		Flexion.db = this;
	}

	private boolean isCollision(int[] links, String query) {
		for(int i = 0; i < links.length; i += 2) {
			if(allFlexionStrings[links[i]].equals(query)) {
				return false;
			}
		}
		return true;
	}

	private List<Word> filterLemmas(int[] refs, String query) {
		List<Word> result = new ArrayList<>();
		for (int ref : refs) {
			if(!isCollision(lemmas[ref], query)) {
				result.add(new Word(lemmas[ref]));
			}
		}
		return result;
	}

	public List<Word> lookup(String query) {
		query = query.toLowerCase().replace('ё', 'е');
		int[] refsToLemmas = refs.get(query.hashCode());
		return refsToLemmas == null ? emptyList() : filterLemmas(refsToLemmas, query);
	}
}
