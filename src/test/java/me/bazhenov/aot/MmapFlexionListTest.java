package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static java.util.Arrays.asList;
import static me.bazhenov.aot.CharacterUtils.dictionaryCharset;
import static me.bazhenov.aot.MmapFlexionList.writeToByteBuffer;
import static me.bazhenov.aot.Utils.writeAndGetBeforePosition;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapFlexionListTest {

	@Test
	public void readWriteCycle() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		List<Flexion> flexion = asList(new Flexion("ae", "а"), new Flexion("ae", "и"));

		int offset = writeAndGetBeforePosition(buffer, writeToByteBuffer(flexion));
		buffer.flip();

		MmapFlexionList flexions = new MmapFlexionList(buffer);

		assertThat(flexions.retrievedNormPostfix(offset), is("а".getBytes(dictionaryCharset)));
	}
}
