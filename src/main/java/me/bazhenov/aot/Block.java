package me.bazhenov.aot;

import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.io.ByteStreams.readFully;
import static com.google.common.primitives.Chars.concat;
import static java.nio.charset.Charset.forName;

public final class Block {

	private static final Charset CHARSET = forName("utf8");

	private int size;
	private char[] commonPrefix;
	private int[] ids;
	private int[] lemmaIds;
	private char[][] postfixes;
	private String[] ancodes;

	public Block(byte[] words) {
		checkNotNull(words);
		ByteArrayInputStream is = new ByteArrayInputStream(words);
		try {
			Header header = Header.fromInputStream(is);
			size = header.size;

			allocateFields();

			commonPrefix = header.commonPrefix.toCharArray();

			for (int i = 0; i < size; i++) {
				readVariation(is, i);
			}

			interFields();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Block(Collection<Variation> variations) {
		checkArgument(!variations.isEmpty());

		commonPrefix = getCommonPrefix(variations).toCharArray();
		size = variations.size();

		allocateFields();

		int i = 0;
		for (Variation v : variations) {
			String postfix = commonPrefix.length <= 0
				? v.getWord()
				: v.getWord().substring(commonPrefix.length);
			postfixes[i] = postfix.toCharArray();
			ids[i] = v.getId();
			lemmaIds[i] = v.getLemmaIndex();
			ancodes[i] = v.getAncode();
			i++;
		}

		interFields();
	}

	private void interFields() {
		for(int i=0; i<size; i++) {
			ancodes[i] = ancodes[i].intern();
		}
	}

	private void allocateFields() {
		ids = new int[size];
		lemmaIds = new int[size];
		postfixes = new char[size][];
		ancodes = new String[size];
	}

	private void writeVariation(OutputStream out, int index) throws IOException {
		byte[] postfix = new String(postfixes[index]).getBytes(CHARSET);
		int id = ids[index];
		int lemmaId = lemmaIds[index];
		byte[] ancode = ancodes[index].getBytes(CHARSET);
		checkState(ancode.length == 4);
		checkState(postfix.length <= 255);

		out.write(Ints.toByteArray(id));
		out.write(Ints.toByteArray(lemmaId));
		out.write(ancode);
		out.write(postfix.length);
		out.write(postfix);
	}

	private void readVariation(InputStream is, int index) throws IOException {
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

		ids[index] = id;
		lemmaIds[index] = lemmaId;
		ancodes[index] = new String(ancode, CHARSET);
		postfixes[index] = new String(w, CHARSET).toCharArray();
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
		return new String(commonPrefix);
	}

	public List<Variation> getAllVariations() {
		List<Variation> variations = newArrayListWithCapacity(size);
		for (int i = 0; i < size; i++) {
			variations.add(createVariation(i));
		}
		return variations;
	}

	private Variation createVariation(int index) {
		checkState(index < size);
		Variation variation = new Variation(new String(concat(commonPrefix, postfixes[index])), ancodes[index], ids[index]);
		variation.setLemmaIndex(lemmaIds[index]);
		return variation;
	}

	public void writeTo(OutputStream result) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new Header(size, new String(commonPrefix)).writeTo(out);
		for (int i = 0; i < size; i++) {
			writeVariation(out, i);
		}
		byte[] payload = out.toByteArray();
		result.write(Shorts.toByteArray(Shorts.checkedCast(payload.length)));
		result.write(payload);
	}

	public static Block readFrom(InputStream input) throws IOException {
		byte[] len = new byte[2];
		readFully(input, len);
		int length = Shorts.fromByteArray(len);
		byte[] block = new byte[length];
		readFully(input, block);
		return new Block(block);
	}

	public String getFirstWord() {
		return new String(concat(commonPrefix, postfixes[0]));
	}

	public int size() {
		return size;
	}

	public List<Variation> getVariations(final String word) {
		return newArrayList(filter(getAllVariations(), new Predicate<Variation>() {
			public boolean apply(Variation input) {
				return input.getWord().equalsIgnoreCase(word);
			}
		}));
	}

	public Variation getVariation(final int id) {
		return find(getAllVariations(), new Predicate<Variation>() {
			public boolean apply(Variation input) {
				return input.getId() == id;
			}
		});
	}

	public int compareFirstWord(String word) {
		return new String(concat(commonPrefix, postfixes[0])).compareTo(word);
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
