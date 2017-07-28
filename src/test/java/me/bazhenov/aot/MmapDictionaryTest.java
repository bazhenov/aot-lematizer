package me.bazhenov.aot;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static me.bazhenov.aot.MmapDictionaryCompilerTest.dictionaryFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class MmapDictionaryTest {

	private MmapDictionary d;

	@BeforeMethod
	public void setUp() throws IOException {
		d = createDictionary();
	}

	@Test
	public void dictionaryShouldBeAbleToFindExistentWords() throws IOException {
		assertThat(d.countWords("краснеющий"), is(1));
		assertThat(d.countWords("дорога"), is(2));
		assertThat(d.countWords("клавиатура"), is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		assertThat(d.countWords("фентифлюшка"), is(0));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		assertThat(d.countWords("человек"), is(1));
		assertThat(d.countWords("люди"), is(1));
		assertThat(d.countWords("ребёнок"), is(1));
		assertThat(d.countWords("дети"), is(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() throws IOException {
		assertThat(d.countWords("super#starnge@string"), is(0));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() throws IOException {
		assertThat(d.getWordNorms("дорога"), hasItems("дорога", "дорогой"));
		assertThat(d.getWordNorms("черномырдину"), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(d.getWordNorms("замок"), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(d.countWords("и"), is(2));
	}


	@Test
	public void regression2() throws IOException {
		assertThat(d.getWordNorms("придет"), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() throws IOException {
		List<String> norms = d.getWordNorms("люди");
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

	public static MmapDictionary createDictionary() throws IOException {
		// Словарь создается в процессе запуска нижеуказанного теста
		if (!dictionaryFile.isFile()) {
			throw new IllegalStateException("Dictionary file missing. Run "
				+ MmapDictionaryCompilerTest.class.getSimpleName());
		}
		return new MmapDictionary(dictionaryFile);
	}
}