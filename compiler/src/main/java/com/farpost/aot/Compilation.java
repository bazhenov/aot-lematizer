package com.farpost.aot;

import com.farpost.aot.mrd.Flexion;
import com.farpost.aot.tab.GrammarInfo;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class Compilation {


	private static void complieLemmas(List<String> lemmas) throws IOException {
		System.out.println("Компилируем леммы...");
		try (final var w = new DataOutputStream(new FileOutputStream("lemmas.bin"))) {
			w.writeInt(lemmas.size());
			for (final var lemma : lemmas) {
				w.write(Compiler.complieString(lemma));
				w.write(Compiler.complieChar('\n'));
			}
		}
		System.out.println("Скомпилировано " + lemmas.size() + " лемм.");
	}

	private static void complieGrammarInfo(List<List<GrammarInfo>> gramlines) throws IOException {
		System.out.println("Компилируем грамматику...");
		try (final var w = new DataOutputStream(new FileOutputStream("grammar.bin"))) {
			w.writeInt(gramlines.size());
			for (final var grammar : gramlines) {
				w.write(Compiler.complieGrammarInfoLine(grammar));
				w.write(Compiler.complieChar('\n'));
			}
		}
		System.out.println("Скомпилировано " + gramlines.size() + " строк грамматики.");
	}

	private static void compileCollisionHashes(Set<Integer> hashes) throws IOException {
		try (var writer = new DataOutputStream(new FileOutputStream("hashes.bin"))) {
			writer.writeInt(hashes.size());
			for (var hash : hashes) {
				writer.write(hash);
			}
		}
	}

	private static void complieCollisionFlexions(List<Flexion> flexions) throws IOException {
		try (var writer = new DataOutputStream(new FileOutputStream("collisions.bin"))) {
			writer.writeInt(flexions.size());
			for (var i : flexions) {
				writer.write(Compiler.complieString(i.sourceString));
				writer.write(Compiler.complieChar('\n'));
				writer.writeInt(i.uniqueLemmaIndex);
				writer.writeInt(i.grammarInfoIndex);
			}
		}
	}

	private static void extractAndCompileCollisionFlexions(List<Flexion> flexions) throws IOException {
		System.out.println("Извлекаем коллизионные флексии для отдельной компиляции...");
		var res = CollisionsFilter.extractCollisions(flexions);
		System.out.println("Обнаружено " + res.removedCollisions.size() + " колллизий.");
		System.out.println("Компилируем хеши коллизионных флексий...");
		compileCollisionHashes(res.collisionHashes);
		System.out.println("Компилируем колизионные флексии...");
		complieCollisionFlexions(res.removedCollisions);
		System.out.println("Скомпилировано " + res.removedCollisions.size() + " коллзионных флексий.");
	}

	private static void compileFlexions(List<Flexion> flexions) throws IOException {
		System.out.println("Всего в наличии " + flexions.size() + " флексий.");
		extractAndCompileCollisionFlexions(flexions);
		System.out.println("Компилируем остальные флексии...");
		try (final var writer = new DataOutputStream(new FileOutputStream("flexions.bin"))) {
			writer.writeInt(flexions.size());
			for (var i : flexions) {
				writer.writeInt(i.flexionHash);
				writer.writeInt(i.uniqueLemmaIndex);
				writer.writeInt(i.grammarInfoIndex);
			}
		}
		System.out.println("Скомпилировано " + flexions.size() + " остальных флексий.");
	}

	public static void run() throws IOException {
		System.out.println("Извлекаем данные из mrd и tab файлов...");
		var data = new AllDataExtractor();
		complieGrammarInfo(data.getGrammarInfoLines());
		complieLemmas(data.getLemmas());
		compileFlexions(data.getFlexions());
	}
}
