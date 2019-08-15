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
		bytes = new byte[4 + size];
		var sizeInBytes = bytesFromInt(size);
		System.arraycopy(sizeInBytes, 0, bytes, 0, 4);
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

	protected static byte[] bytesFromInt(int number) {
		return new byte[]{
			(byte) ((number & 0xFF000000) >> 24),
			(byte) ((number & 0x00FF0000) >> 16),
			(byte) ((number & 0x0000FF00) >> 8),
			(byte) ((number & 0x000000FF) >> 0)
		};
	}
}
