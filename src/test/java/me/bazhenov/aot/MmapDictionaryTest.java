package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

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
	public void dictionaryShouldBeAbleToFindExistentWords() throws IOException {
		MmapDictionary d = createDictionary();
		assertThat(d.countWords("краснеющий"), is(1));
		assertThat(d.countWords("дорога"), is(2));
		assertThat(d.countWords("клавиатура"), is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		MmapDictionary d = createDictionary();
		assertThat(d.countWords("фентифлюшка"), is(0));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		MmapDictionary d = createDictionary();
		assertThat(d.countWords("человек"), is(1));
		assertThat(d.countWords("люди"), is(1));
		assertThat(d.countWords("ребёнок"), is(1));
		assertThat(d.countWords("дети"), is(1));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() throws IOException {
		MmapDictionary d = createDictionary();
		List<String> norms = d.getWordNorms("дорога");
		assertThat(norms, hasSize(2));
		assertThat(norms, hasItems("дорога"));
		assertThat(norms, hasItems("дорогой"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() throws IOException {
		MmapDictionary d = createDictionary();
		List<String> norms = d.getWordNorms("люди");
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

	private MmapDictionary createDictionary() throws IOException {
		File dictFile = new File("/Users/bazhenov/Desktop/dictionary.dict");
		return new MmapDictionary(dictFile);
	}
}