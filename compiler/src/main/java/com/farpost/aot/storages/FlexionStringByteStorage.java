package com.farpost.aot.storages;


/**
 * Хранилище байт для компиляции в память специально для строк флексий; знает свой размер
 */
public class FlexionStringByteStorage extends ByteStorage {

	FlexionStringByteStorage() {
		super(63111999);
	}
}
