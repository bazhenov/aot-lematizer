package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import me.bazhenov.Utils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


final class Compiler {

	public static void main(String[] args) throws IOException {
		System.out.println("Парсим /mrd . . .");
		final var store = new FlexionStorage();
		final var normal = store.getAllFlexion();
		System.out.println(String.format("Всех флексий %d", normal.size()));
		System.out.println("Извлекаем коллизии . . .");
		final var removed = CollisionsFilter.extractCollisions(normal).removedCollisions;
		System.out.println(String.format("Извлечено колизионных флексий %d", removed.size()));
		System.out.println(String.format("Осталось нормальных флексий %d", normal.size()));
		System.out.println(String.format("Всех флексий теперь %d", removed.size() + normal.size()));
		System.out.print("Компилируем /mrd");
		try (final var writer = new DataOutputStream(new FileOutputStream("library/src/main/resources/MRD.BIN"))) {
			writer.writeInt(removed.size());
			for (final var wrongFlexion : removed) {
				writer.writeInt(wrongFlexion.lemmaIndex);
				writer.writeInt(wrongFlexion.grammarIndex);
				writer.write(bytesFromString(wrongFlexion.source));
			}
			System.out.print(" .");
			writer.writeInt(normal.size());
			for (final var normFlexion : normal) {
				writer.writeInt(normFlexion.lemmaIndex);
				writer.writeInt(normFlexion.grammarIndex);
				writer.writeInt(normFlexion.source.hashCode());
			}
			System.out.print(" .");
			writer.writeInt(store.getAllLemmas().size());
			for (final var lemma : store.getAllLemmas()) {
				writer.write(bytesFromString(lemma));
			}
			System.out.print(" .");
			writer.writeInt(store.getAllGrammarVariants().size());
			for (final var gram : store.getAllGrammarVariants()) {
				writer.write(bytesFromGrammars(gram));
			}
		}
		System.out.println("\nКомпиляция успешно завершена: library/src/main/resources/MRD.BIN");
	}

	private static byte byteFromChar(final char n) {
		return n == '\n' ? 100 : Utils.safeCharToByte(n);
	}

	private static byte[] bytesFromGrammars(final List<GrammarInfo> line) {
		final var res = new byte[line.size() + 1];
		for (var i = 0; i < (res.length - 1); ++i) {
			res[i] = (byte) Arrays.binarySearch(GrammarInfo.values(), line.get(i));
		}
		res[line.size()] = byteFromChar('\n');
		return res;
	}

	private static byte[] bytesFromString(final String s) {
		final var res = new byte[s.length() + 1];
		for (var i = 0; i < (res.length - 1); ++i) {
			res[i] = byteFromChar(s.charAt(i));
		}
		res[s.length()] = byteFromChar('\n');
		return res;
	}
}
