package me.bazhenov.aot.codec;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;

public class DictionaryCompressor {

	public static void writeTo(String fileName, InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		String prevWord = null;

		Multimap<Integer, Short> info = HashMultimap.create();
		Block block = new Block();

		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\t");
			int id = parseInt(parts[0]);
			int lemmaId = parseInt(parts[2]);
			String word = parts[1];
			String morphInfo = parts[4];

			if (prevWord == null || !word.equalsIgnoreCase(prevWord)) {
				block.addInfo(prevWord, info);
				System.out.println(prevWord + "\t" + info);
				info.clear();
			}

			info.put(lemmaId, (short)0);
			prevWord = word;
		}
	}
}
