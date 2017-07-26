package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;

import static me.bazhenov.aot.MmapFixedWidthIntBlock.writeToByteBuffer;
import static me.bazhenov.aot.Utils.writeAndGetBeforePosition;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapFixedWidthIntBlockTest {

	@Test
	public void readWriteCycle() {
		ByteBuffer buffer = ByteBuffer.allocate(12);

		writeAndGetBeforePosition(buffer, writeToByteBuffer(15));
		writeAndGetBeforePosition(buffer, writeToByteBuffer(13));
		buffer.flip();

		MmapFixedWidthIntBlock block = new MmapFixedWidthIntBlock(buffer);
		assertThat(block.getValue(0), is(15));
		assertThat(block.getValue(1), is(13));
	}
}
