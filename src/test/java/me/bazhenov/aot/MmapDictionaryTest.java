package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapDictionaryTest {

	@Test
	public void testCompileLoad() throws IOException {
		File tempFile = File.createTempFile("mmap", "dict");
		tempFile.deleteOnExit();

		tempFile = new File("/Users/bazhenov/Desktop/dictionary.dict");

		MmapDictionaryCompiler.compile(tempFile);
		System.out.println(tempFile);
	}

	@Test
	public void testReadingDictionary() throws IOException {
		File dictFile = new File("/Users/bazhenov/Desktop/dictionary.dict");
		MmapDictionary d = new MmapDictionary(dictFile);
		assertThat(d.checkExists("краснеющий"), is(true));
		assertThat(d.checkExists("фентифлюшка"), is(false));
	}
}