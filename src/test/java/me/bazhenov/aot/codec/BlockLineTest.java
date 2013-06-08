package me.bazhenov.aot.codec;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;

import static me.bazhenov.aot.codec.BlockLine.readFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BlockLineTest {

	@Test
	public static void shouldBeAbleToWriteAndReadBlockLines() {
		BlockLine line = new BlockLine("ый", new BlockLine.MorphRef(6, new short[]{1, 6, 7}),
			new BlockLine.MorphRef(8, new short[]{2, 7, 8}));
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		line.writeTo(buffer);
		buffer.flip();

		BlockLine lineCopy = readFrom(buffer);
		assertThat(lineCopy, equalTo(line));
	}
}
