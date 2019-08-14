package com.farpost.aot.compilers;

import com.farpost.aot.Bytecode;
import com.farpost.aot.CompilerFabric;
import com.farpost.aot.Flexion;
import com.farpost.aot.MorphologyTag;

import java.io.IOException;
import java.util.List;

/**
 * Компилятор наборов флексий (лемм)
 */
public class LemmaCompiler extends Compiler<Flexion[]> {
	private final Compiler<String>
		flexionCompiler = CompilerFabric.createCompilerFor(String.class);
	private final Compiler<MorphologyTag[]>
		morphologyCompiler = CompilerFabric.createCompilerFor(MorphologyTag[].class);

	public LemmaCompiler() throws IOException {
		super("target/LEMMAS.BIN");
	}

	@Override
	protected int pushBytes(Flexion[] line) throws IOException {
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
