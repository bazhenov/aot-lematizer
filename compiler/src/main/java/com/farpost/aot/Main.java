package com.farpost.aot;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;

public final class Main {

	public static void main(String[] args) throws IOException {
		if (new File(args[0]).exists()) {
			out.println("Mrd-file already compiled to " + args[0]);
			return;
		}
		out.println("Reading...");
		var zippedLemmas = Zip.zip(LemmasReader.readLemmas());
		out.println("Compilation [1..4]");
		try (var file = new DataOutputStream(new FileOutputStream(args[0]))) {
			out.println("1. Morphology..");
			compileMorphology(file, zippedLemmas.getMorph());
			out.println("2. Strings..");
			compileStrings(file, zippedLemmas.getStrings());
			out.println("3. Lemma indexes..");
			compileLemmas(file, zippedLemmas.getLemmas());
			out.println("4. Flexion hashes..");
			compileHashes(file, Optimizer.optimize(zippedLemmas));
		}
		out.println("Mrd-file successfully compiled to " + args[0]);
	}

	private static void compileMorphology(DataOutputStream file, List<Set<MorphologyTag>> morph) throws IOException {
		file.writeInt(morph.size());
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

	private static void compileStrings(DataOutputStream file, List<String> strings) throws IOException {
		file.writeInt(strings.size());
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

	private static void compileLemmas(DataOutputStream file, List<List<MiniFlexion>> lst) throws IOException {
		file.writeInt(lst.size());
		for (var lemma : lst) {
			file.writeInt(lemma.size());
			for (var flexion : lemma) {
				file.writeInt(flexion.getStringIndex());
				file.writeInt(flexion.getGrammarIndex());
			}
		}
	}

	private static void compileHashes(DataOutputStream file, Map<Integer, Set<Integer>> hashes) throws IOException {
		file.writeInt(hashes.size());
		for (var pair : hashes.entrySet()) {
			file.writeInt(pair.getValue().size() + 1);
			file.writeInt(pair.getKey());
			for (var index : pair.getValue()) {
				file.writeInt(index);
			}
		}
	}
}
