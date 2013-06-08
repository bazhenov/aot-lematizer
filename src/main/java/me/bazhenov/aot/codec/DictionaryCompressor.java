package me.bazhenov.aot.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Shorts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;

public class DictionaryCompressor {

	public static void writeTo(String fileName, InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		String prevWord = null;

		Multimap<Integer, Short> info = HashMultimap.create();
		Map<String, Integer> ancodes = newHashMap();
		Block block = new Block();

		BlockLine blockLine;

		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\t");
			int id = parseInt(parts[0]);
			int lemmaId = parseInt(parts[2]);
			String word = parts[1];
			String morphInfo = parts[4];
			short ancode = Shorts.checkedCast(index(ancodes, morphInfo));

			if (prevWord != null && !word.equalsIgnoreCase(prevWord)) {
				block.addInfo(prevWord, info);
				System.out.println(prevWord + "\t" + info);
				info.clear();
			}

			info.put(lemmaId, (short) 0);
			prevWord = word;
		}
	}

	public static <T> int index(Map<T, Integer> map, T obj) {
		if (!map.containsKey(obj)) {
			map.put(obj, map.size() + 1);
		}
		return map.get(obj);
	}
}
