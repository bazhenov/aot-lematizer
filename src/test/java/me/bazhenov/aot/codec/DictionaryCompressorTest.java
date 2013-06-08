package me.bazhenov.aot.codec;

import org.testng.annotations.Test;

import java.io.IOException;

public class DictionaryCompressorTest {

	@Test
	public void shouldBeAbleToBuildCompressedDictionary() throws IOException {
		DictionaryCompressor.writeTo("result.dict", getClass().getResourceAsStream("/dict.dump"));
	}
}
