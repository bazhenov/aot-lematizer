package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class Utils {

	public static int writeAndGetBeforePosition(ByteBuffer buffer, Consumer<ByteBuffer> writer) {
		int position = buffer.position();
		writer.accept(buffer);
		return position;
	}

	public static ByteBuffer checkBufferIsReset(ByteBuffer buffer) {
		if (buffer.position() != 0)
			throw new IllegalArgumentException("Buffer should have zero position");
		return buffer;
	}

	static void checkPositive(int l) {
		if (l <= 0) {
			throw new IllegalStateException("Should be positive number: " + l);
		}
	}
}
