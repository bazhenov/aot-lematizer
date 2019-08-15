package com.farpost.aot.storages;


import com.farpost.aot.Bytecode;
import com.farpost.aot.Utils;

/**
 * Хранилище байт для компиляции в память специально для строк флексий; знает свой размер
 */
public class FlexionStringByteStorage extends ByteStorage {

	public FlexionStringByteStorage() {
		super(63111999);
	}

	public void addString(String s) {
		var res = new byte[s.length() + 1];
		for (var i = 0; i < s.length(); ++i) {
			res[i] = Utils.charToByte(s.charAt(i));
		}
		res[s.length()] = Bytecode.endOfCompiledLine;
		addBytes(res);
	}
}
