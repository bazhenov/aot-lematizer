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

		MmapDictionary d = new MmapDictionary(tempFile);
		assertThat(d.countWords("краснеющий"), is(1));
		assertThat(d.countWords("фентифлюшка"), is(0));
	}

	@Test
	public void testReadingDictionary() throws IOException {
		MmapDictionary d = createDictionary();
		assertThat(d.countWords("краснеющий"), is(1));
		assertThat(d.countWords("фентифлюшка"), is(0));
	}

	@Test
	public void testEmptyWOrdBases() throws IOException {
		MmapDictionary d = createDictionary();
		assertThat(d.countWords("человек"), is(1));
		assertThat(d.countWords("люди"), is(1));
		assertThat(d.countWords("ребёнок"), is(1));
		assertThat(d.countWords("дети"), is(1));
	}

	private MmapDictionary createDictionary() throws IOException {
		File dictFile = new File("/Users/bazhenov/Desktop/dictionary.dict");
		return new MmapDictionary(dictFile);
	}
}