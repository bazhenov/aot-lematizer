package com.farpost.aot.compilers;

import com.farpost.aot.Bytecode;
import com.farpost.aot.MorphologyTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Компилятор наборов тегов
 */
public class MorphologyTagCompiler extends Compiler<Set<MorphologyTag>> {

	public MorphologyTagCompiler() throws IOException {
		super("target/GRAMMAR.BIN");
	}

	@Override
	protected int pushBytes(Set<MorphologyTag> line) throws IOException {
		var bytes = new byte[line.size() + 1];
		var index = -1;
		for(var i: line) {
			bytes[++index] =  (byte) Arrays.binarySearch(MorphologyTag.values(), i);
		}
		bytes[line.size()] = Bytecode.endOfCompiledLine;
		writer.write(bytes);
		return line.size();
	}
}
