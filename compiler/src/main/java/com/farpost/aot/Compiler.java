package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.data.MorphologyTag;
import me.bazhenov.aot.Utils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


final class Compiler {

	public static void main(String[] args) throws IOException {
		var data = InputData.prepare();
		System.out.print("Compiling /mrd");
		try (var writer = new DataOutputStream(new FileOutputStream("library/src/main/resources/MRD.BIN"))) {

			compileWrongFlexions(writer, data.getCollisionFlexions());
			compileNormalFlexions(writer, data.getNormalFlexions());
			compileLemmas(writer, data.getAllLemmas());
			compileGrammar(writer, data.getGrammarInfoVariants());

		}
		System.out.println("\nCompilation completed successfully: library/src/main/resources/MRD.BIN");
	}


	private static void compileWrongFlexions(DataOutputStream writer, Collection<Flexion> removed) throws IOException {
		writer.writeInt(removed.size());
		for (var wrongFlexion : removed) {
			writer.writeInt(wrongFlexion.lemmaIndex);
			writer.writeInt(wrongFlexion.grammarIndex);
			writer.write(bytesFromString(wrongFlexion.source));
		}
		System.out.print(" .");
	}


	private static void compileNormalFlexions(DataOutputStream writer, Collection<Flexion> normal) throws IOException {
		writer.writeInt(normal.size());
		for (var normFlexion : normal) {
			writer.writeInt(normFlexion.lemmaIndex);
			writer.writeInt(normFlexion.grammarIndex);
			writer.writeInt(normFlexion.source.hashCode());
		}
		System.out.print(" .");
	}


	private static void compileLemmas(DataOutputStream writer, Collection<String> lemmas) throws IOException {
		writer.writeInt(lemmas.size());
		for (var lemma : lemmas) {
			writer.write(bytesFromString(lemma));
		}
		System.out.print(" .");
	}


	private static void compileGrammar(DataOutputStream writer, Collection<List<MorphologyTag>> grammar)
		throws IOException {
		writer.writeInt(grammar.size());
		for (var gram : grammar) {
			writer.write(bytesFromGrammars(gram));
		}
	}


	private static byte byteFromChar(final char n) {
		return n == '\n' ? 100 : Utils.safeCharToByte(n);
	}


	private static byte[] bytesFromGrammars(final List<MorphologyTag> line) throws UncheckedIOException {

		if(MorphologyTag.values().length >= 100) {
			throw new UncheckedIOException(new IOException("GrammarInfo.values() >= 100"));
		}

		var res = new byte[line.size() + 1];
		for (var i = 0; i < (res.length - 1); ++i) {
			res[i] = (byte) Arrays.binarySearch(MorphologyTag.values(), line.get(i));
		}
		res[line.size()] = byteFromChar('\n');
		return res;
	}


	private static byte[] bytesFromString(final String s) {
		var res = new byte[s.length() + 1];
		for (var i = 0; i < (res.length - 1); ++i) {
			res[i] = byteFromChar(s.charAt(i));
		}
		res[s.length()] = byteFromChar('\n');
		return res;
	}
}
