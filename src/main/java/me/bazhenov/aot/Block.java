package me.bazhenov.aot;

import com.google.common.primitives.Ints;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static com.google.common.io.ByteStreams.readFully;
import static java.nio.charset.Charset.forName;

public final class Block {

	private final byte[] words;
	private final String commonPrefix;
	private static final Charset charset = forName("utf8");

	public Block(Collection<Variation> variations) {
		checkArgument(!variations.isEmpty());

		commonPrefix = getCommonPrefix(variations);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (Variation v : variations) {
				writeVariation(out, v);
			}
			words = out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeVariation(OutputStream out, Variation v) throws IOException {
		String originalWord = v.getWord();
		String word = commonPrefix.isEmpty()
			? originalWord
			: originalWord.substring(commonPrefix.length());
		byte[] wordBytes = word.getBytes(charset);
		int id = v.getId();
		int lemmaId = v.getLemmaIndex();
		byte[] ancode = v.getAncode().getBytes(charset);
		int wordLength = wordBytes.length + 4 + 4 + ancode.length;
		checkState(wordLength <= 255);
		out.write(Ints.toByteArray(id));
		out.write(Ints.toByteArray(lemmaId));
		out.write(ancode.length);
		out.write(ancode);
		out.write(wordBytes.length);
		out.write(wordBytes);
	}

	private static String getCommonPrefix(Collection<Variation> variations) {
		String first = get(variations, 0).getWord();

		StringBuilder commonPrefix = new StringBuilder();
		for (int i = 0; i < first.length(); i++) {
			char c = first.charAt(i);
			boolean allTheSame = true;
			for (Variation v : variations) {
				String s = v.getWord();
				if (i >= s.length() || s.charAt(i) != c) {
					allTheSame = false;
					break;
				}
			}
			if (allTheSame) {
				commonPrefix.append(c);
			} else {
				break;
			}
		}
		return commonPrefix.toString();
	}

	public String getCommonPrefix() {
		return commonPrefix;
	}

	public Variation getVariationAtOffset(int offset) {
		checkArgument(offset < words.length);
		ByteArrayInputStream is = new ByteArrayInputStream(words);
		Variation v = null;
		while (offset-- >= 0) {
			try {
				v = readVariation(commonPrefix, is);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return v;
	}

	private Variation readVariation(String commonPrefix, InputStream is) throws IOException {
		byte[] word = new byte[4];

		readFully(is, word);
		int id = Ints.fromByteArray(word);

		readFully(is, word);
		int lemmaId = Ints.fromByteArray(word);


		int ancodeLength = is.read();
		byte[] ancode = new byte[ancodeLength];
		readFully(is, ancode);

		int wLength = is.read();
		byte[] w = new byte[wLength];
		readFully(is, w);

		Variation var = new Variation(commonPrefix + new String(w, charset), new String(ancode, charset), id);
		var.setLemmaIndex(lemmaId);
		return var;
	}


}
