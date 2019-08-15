package com.farpost.aot.storages;

/**
 * Хранилище байт (для компиляции в память)
 */
abstract class ByteStorage {

	private final byte[] bytes;
	private int index = -1;

	/**
	 * Создаёт массив размером (4 + size) байт
	 * В первые четыре байта записано число size
	 *
	 * @param size количество байт
	 */
	ByteStorage(int size) {
		bytes = new byte[size + 4];
		bytes[0] = (byte) ((size & 0xFF000000) >> 24);
		bytes[1] = (byte) ((size & 0x00FF0000) >> 16);
		bytes[2] = (byte) ((size & 0x0000FF00) >> 8);
		bytes[3] = (byte) ((size & 0x000000FF) >> 0);
	}

	public void addBytes(byte[] newBytes) {
		System.arraycopy(newBytes, 0, bytes, index + 1, newBytes.length);
	}

	/**
	 * @return Возвращает все записанные байты
	 */
	public byte[] getAllBytes() {
		return bytes;
	}
}
