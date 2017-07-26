package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static me.bazhenov.aot.Utils.checkBufferIsReset;

public class MmapFixedWidthIntBlock {

	private ByteBuffer buffer;

	public MmapFixedWidthIntBlock(ByteBuffer buffer) {
		this.buffer = checkBufferIsReset(buffer);
	}

	public static Consumer<ByteBuffer> writeToByteBuffer(int value) {
		return b -> {
			b.putInt(value);
		};
	}

	public int getValue(int offset) {
		return buffer.getInt(offset * 4);
	}
}
