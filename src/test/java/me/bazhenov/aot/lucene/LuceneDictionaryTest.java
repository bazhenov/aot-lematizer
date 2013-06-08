package me.bazhenov.aot.lucene;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static com.google.common.io.Files.createTempDir;
import static me.bazhenov.aot.PartOfSpeech.Adjective;
import static me.bazhenov.aot.PartOfSpeech.Noun;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class LuceneDictionaryTest {

	@Test
	public void compileAndLookupCycle() throws IOException {
		InputStream stream = getClass().getResourceAsStream("/dict.dump");
		File indexDir = createTempDir();
		DictionaryIndexer indexer = new DictionaryIndexer(stream, indexDir);
		indexer.index();

		LuceneDictionary d = new LuceneDictionary(indexDir);
		Collection<Morph> result = d.lookup("анархистах");
		assertThat(result, contains(new Morph("анархист", Noun)));

		result = d.lookup("анархистским");
		assertThat(result, contains(new Morph("анархистский", Adjective)));
	}
}
