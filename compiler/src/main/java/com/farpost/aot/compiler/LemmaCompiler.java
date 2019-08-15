package com.farpost.aot.compiler;

import com.farpost.aot.Bytecode;
import com.farpost.aot.Flexion;
import com.farpost.aot.MorphologyTag;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Компилятор наборов флексий (лемм)
 */
public class LemmaCompiler extends Compiler<Collection<Flexion>> {
	private final Compiler<String> flexionCompiler = new FlexionStringsCompiler();
	private final Compiler<Set<MorphologyTag>> morphologyCompiler = new MorphologyTagCompiler();

	public LemmaCompiler() throws IOException {
		super("target/LEMMAS.BIN");
	}

	@Override
	protected int pushBytes(Collection<Flexion> line) throws IOException {
		int bytesCounter = 0;
		for(var flexion: line) {
			writer.writeInt(flexionCompiler.compile(flexion.getString()));
			writer.writeInt(morphologyCompiler.compile(flexion.getTags()));
			bytesCounter += 8;
		}
		writer.writeByte(Bytecode.endOfCompiledLine);
		return bytesCounter;
	}
}
