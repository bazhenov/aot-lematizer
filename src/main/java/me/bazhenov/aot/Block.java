package me.bazhenov.aot;

import com.google.common.primitives.Ints;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.io.ByteStreams.readFully;
import static java.nio.charset.Charset.forName;

public final class Block {

	private final byte[] words;
	private static final Charset CHARSET = forName("utf8");

	public Block(Collection<Variation> variations) {
		checkArgument(!variations.isEmpty());

		String commonPrefix = getCommonPrefix(variations);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = commonPrefix.getBytes(CHARSET);
			new Header(variations.size(), commonPrefix).writeTo(out);

			for (Variation v : variations) {
				writeVariation(out, v, commonPrefix);
			}
			words = out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeVariation(OutputStream out, Variation v, String commonPrefix) throws IOException {
		String originalWord = v.getWord();
		String word = commonPrefix.isEmpty()
			? originalWord
			: originalWord.substring(commonPrefix.length());
		byte[] wordBytes = word.getBytes(CHARSET);
		int id = v.getId();
		int lemmaId = v.getLemmaIndex();
		byte[] ancode = v.getAncode().getBytes(CHARSET);
		checkState(ancode.length == 4);
		checkState(wordBytes.length <= 255);

		out.write(Ints.toByteArray(id));
		out.write(Ints.toByteArray(lemmaId));
		out.write(ancode);
		out.write(wordBytes.length);
		out.write(wordBytes);
	}

	private Variation readVariation(String commonPrefix, InputStream is) throws IOException {
		byte[] word = new byte[4];

		readFully(is, word);
		int id = Ints.fromByteArray(word);

		readFully(is, word);
		int lemmaId = Ints.fromByteArray(word);

		byte[] ancode = new byte[4];
		readFully(is, ancode);

		int wLength = is.read();
		byte[] w = new byte[wLength];
		readFully(is, w);

		Variation var = new Variation(commonPrefix + new String(w, CHARSET), new String(ancode, CHARSET), id);
		var.setLemmaIndex(lemmaId);
		return var;
	}

	/**
	 * @param variations список слово форм
	 * @return общий префикс для всех строк
	 */
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
		ByteArrayInputStream is = new ByteArrayInputStream(words);
		try {
			return Header.fromInputStream(is).commonPrefix;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Variation getVariationAtOffset(int offset) {
		checkArgument(offset < words.length);
		ByteArrayInputStream is = new ByteArrayInputStream(words);
		Variation v = null;
		try {
			String commonPrefix = Header.fromInputStream(is).commonPrefix;
			while (offset-- >= 0) {
				v = readVariation(commonPrefix, is);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return v;
	}

	public List<Variation> getAllVariations() {
		try {
			InputStream stream = new ByteArrayInputStream(words);
			Header header = Header.fromInputStream(stream);
			String commonPrefix = header.commonPrefix;
			List<Variation> variations = newArrayListWithCapacity(header.size);
			for (int i=0; i<header.size; i++) {
				variations.add(readVariation(commonPrefix, stream));
			}
			return variations;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeTo(OutputStream result) throws IOException {
		result.write(words);
	}

	public String getFirstWord() {
		return getVariationAtOffset(0).getWord();
	}

	public int size() {
		try {
			return Header.fromInputStream(new ByteArrayInputStream(words)).size;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static private class Header {
		private final int size;
		private final String commonPrefix;

		private Header(int size, String commonPrefix) {
			this.size = size;
			this.commonPrefix = commonPrefix;
		}

		private static Header fromInputStream(InputStream stream) throws IOException {
			int size = stream.read();
			int wLength = stream.read();
			byte[] w = new byte[wLength];
			readFully(stream, w);
			return new Header(size, new String(w, CHARSET));
		}

		private void writeTo(OutputStream stream) throws IOException {
			checkState(size <= 255);
			byte[] bytes = commonPrefix.getBytes(CHARSET);
			checkState(bytes.length <= 255);
			stream.write(size);
			stream.write(bytes.length);
			stream.write(bytes);
		}
	}
}
