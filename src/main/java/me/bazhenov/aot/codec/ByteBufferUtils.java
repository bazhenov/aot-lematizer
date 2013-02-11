package me.bazhenov.aot.codec;

public class ByteBufferUtils {

	public static byte checkedCast(int value) {
		if (value > Byte.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		return (byte) value;
	}
}
