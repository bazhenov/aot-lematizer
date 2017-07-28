package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static me.bazhenov.aot.MmapFlexionList.writeToByteBuffer;
import static me.bazhenov.aot.Utils.writeAndGetBeforePosition;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapFlexionListTest {

	private static Charset dictionaryCharset = Charset.forName("windows-1251");

	@Test
	public void readWriteCycle() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		List<Flexion> flexion = asList(new Flexion("ae", "а"), new Flexion("ae", "и"));

		int offset = writeAndGetBeforePosition(buffer, writeToByteBuffer(flexion));
		buffer.flip();

		MmapFlexionList flexions = new MmapFlexionList(buffer);

		assertThat(flexions.retrievedNormPostfix(offset), is("а".getBytes(dictionaryCharset)));
	}

	@Test
	public void emptyEnding() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		List<Flexion> flexion = singletonList(new Flexion("ae", ""));

		int offset = writeAndGetBeforePosition(buffer, writeToByteBuffer(flexion));
		buffer.flip();

		MmapFlexionList flexions = new MmapFlexionList(buffer);

		assertThat(flexions.retrievedNormPostfix(offset), is("".getBytes(dictionaryCharset)));
	}
}
