package com.farpost.aot.compiler;

import com.farpost.aot.Bytecode;

import java.io.IOException;

/**
 * Компилятор строк флексий
 */
public class FlexionStringsCompiler extends Compiler<String> {

	public FlexionStringsCompiler() throws IOException {
		super("target/FLEXION_STRINGS.BIN");
	}


	@Override
	protected int pushBytes(String s) throws IOException {
		var bytes = new byte[s.length() + 1];
		for(var i = 0; i < s.length(); ++i) {
			bytes[i] = Utils.charToByte(s.charAt(i));
		}
		bytes[s.length()] = Bytecode.endOfCompiledLine;
		writer.write(bytes);
		return bytes.length;
	}
}
