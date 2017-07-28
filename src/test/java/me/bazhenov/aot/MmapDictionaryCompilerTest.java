package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static me.bazhenov.aot.MmapDictionaryCompiler.compileInto;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapDictionaryCompilerTest {

	static final File dictionaryFile = new File(System.getProperty("java.io.tmpdir"), "aot.dict");

	@Test(groups = "compiler")
	public void testCompileLoad() throws IOException {
		compileInto(dictionaryFile);
		System.out.println("Dictionary written to: " + dictionaryFile.getAbsolutePath());

		MmapDictionary d = new MmapDictionary(dictionaryFile);
		assertThat(d.countWords("краснеющий"), is(1));
		assertThat(d.countWords("фентифлюшка"), is(0));
	}
}