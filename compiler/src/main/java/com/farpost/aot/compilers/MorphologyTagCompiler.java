package com.farpost.aot.compilers;

import com.farpost.aot.Bytecode;
import com.farpost.aot.MorphologyTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Компилятор наборов тегов
 */
public class MorphologyTagCompiler extends Compiler<List<MorphologyTag>> {

	public MorphologyTagCompiler() throws IOException {
		super("target/GRAMMAR.BIN");
	}

	@Override
	protected int pushBytes(List<MorphologyTag> line) throws IOException {
		var bytes = new byte[line.size() + 1];
		for(var i = 0; i < line.size(); ++i) {
			bytes[i] =  (byte) Arrays.binarySearch(MorphologyTag.values(), line.get(i));
		}
		bytes[line.size()] = Bytecode.endOfCompiledLine;
		writer.write(bytes);
		return line.size();
	}
}
