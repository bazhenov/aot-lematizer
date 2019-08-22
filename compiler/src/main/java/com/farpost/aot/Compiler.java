package com.farpost.aot;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;

public final class Compiler {

	public static void main(String[] args) throws IOException {
		if (new File(args[0]).exists()) {
			out.println("Mrd-file already compiled to " + args[0]);
			return;
		}
		out.println("Reading...");
		var zippedLemmas = Zip.zip(LemmasReader.readLemmas());
		out.println("Compilation [1..4]");
		try (var file = new DataOutputStream(new FileOutputStream(args[0]))) {
			out.println("1. Morphology (" + zippedLemmas.getMorph().size() + ")");
			compileMorphology(file, zippedLemmas.getMorph());
			out.println("2. Strings (" + zippedLemmas.getStrings().size() + ")");
			compileStrings(file, zippedLemmas.getStrings());
			out.println("3. Lemma indexes (" + zippedLemmas.getLemmas().size() + ")");
			compileLemmas(file, zippedLemmas.getLemmas());
			var hashes = Hashes.calculate(zippedLemmas);
			out.println("4. Flexion hashes (" + hashes.size() + ")");
			compileHashes(file, hashes);
		}
		out.println("Mrd-file successfully compiled to " + args[0]);
	}

	// 1
	private static void compileMorphology(DataOutputStream file, List<Set<MorphologyTag>> morph) throws IOException {
		file.writeInt(morph.size());

		int sizeInBytes = 0;
		for (var m : morph) {
			sizeInBytes += m.size();
			sizeInBytes += 1;
		}
		file.writeInt(sizeInBytes);

		for (var m : morph) {
			file.write(bytesFromMorphology(m));
		}
	}

	private static byte[] bytesFromMorphology(Set<MorphologyTag> line) throws UncheckedIOException {
		if (MorphologyTag.values().length >= Bytecode.endOfCompiledLine) {
			throw new UncheckedIOException(new IOException("GrammarInfo.values() >= " + Bytecode.endOfCompiledLine));
		}
		var res = new byte[line.size() + 1];
		var resIndex = -1;
		for (var m : line) {
			res[++resIndex] = (byte) Arrays.binarySearch(MorphologyTag.values(), m);
		}
		res[line.size()] = byteFromChar('\n');
		return res;
	}

	private static byte byteFromChar(char n) {
		return n == '\n' ? Bytecode.endOfCompiledLine : Utils.safeCharToByte(n);
	}

	// 2
	private static void compileStrings(DataOutputStream file, List<String> strings) throws IOException {
		file.writeInt(strings.size());

		int sizeInBytes = 0;
		for (var s : strings) {
			sizeInBytes += s.length();
			sizeInBytes += 1;
		}
		file.writeInt(sizeInBytes);

		for (var str : strings) {
			file.write(bytesFromString(str));
		}
	}

	private static byte[] bytesFromString(String s) {
		var res = new byte[s.length() + 1];
		for (var i = 0; i < (res.length - 1); ++i) {
			res[i] = byteFromChar(s.charAt(i));
		}
		res[s.length()] = byteFromChar('\n');
		return res;
	}

	// 3
	private static void compileLemmas(DataOutputStream file, List<List<MiniFlexion>> lst) throws IOException {
		file.writeInt(lst.size());

		int sizeInBytes = 0;
		for (var l : lst) {
			sizeInBytes += 4;
			for (var f : l) {
				sizeInBytes += 8;
			}
		}
		file.writeInt(sizeInBytes);

		for (var lemma : lst) {
			file.writeInt(lemma.size());
			for (var flexion : lemma) {
				file.writeInt(flexion.getStringIndex());
				file.writeInt(flexion.getMorphIndex());
			}
		}
	}

	// 4
	private static void compileHashes(DataOutputStream file, Map<Integer, Set<Integer>> hashes) throws IOException {
		file.writeInt(hashes.size());

		int sizeInBytes = 0;
		for (var pair : hashes.entrySet()) {
			// хеш
			sizeInBytes += 4;
			// кол-во индексов
			sizeInBytes += 4;
			// сами индексы
			sizeInBytes += (pair.getValue().size() * 4);
		}
		file.writeInt(sizeInBytes);

		for (var pair : hashes.entrySet()) {
			// хеш
			file.writeInt(pair.getKey());
			// кол-во индексов
			file.writeInt(pair.getValue().size());
			// индексы
			for (var index : pair.getValue()) {
				file.writeInt(index);
			}

		}
	}
}
