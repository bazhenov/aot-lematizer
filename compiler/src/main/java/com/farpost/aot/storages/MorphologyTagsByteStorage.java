package com.farpost.aot.storages;

import com.farpost.aot.Bytecode;
import com.farpost.aot.MorphologyTag;

import java.util.Arrays;
import java.util.List;

/**
 * Хранилище байт для компиляции морфологических тегов; знает свой размер
 */
public class MorphologyTagsByteStorage extends ByteStorage {

	MorphologyTagsByteStorage() {
		super(34075106);
	}

	public void addTags(List<MorphologyTag> tags) {
		var bytes = new byte[tags.size() + 1];
		for (var i = 0; i < tags.size(); ++i) {
			bytes[i] = (byte) Arrays.binarySearch(MorphologyTag.values(), tags.get(i));
		}
		bytes[tags.size()] = Bytecode.endOfCompiledLine;
		addBytes(bytes);
	}
}
