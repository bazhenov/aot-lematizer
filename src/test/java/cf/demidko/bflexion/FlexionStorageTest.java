package cf.demidko.bflexion;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionStorageTest {

	private FlexionStorage map;

	public FlexionStorageTest() throws IOException {
		map = new FlexionStorage();
	}

	public static Set<String> collectLemmas(final Flexion[] results) {
		return Arrays.stream(results).map(x -> x.lemma).collect(Collectors.toSet());
	}

	public static List<Set<GrammarInfo>> collectGrammarInfo(final Flexion[] results) {
		return Arrays.stream(results)
			.map(x ->
				Arrays.stream(x.grammarInfo)
					.collect(Collectors.toSet()))
			.collect(Collectors.toList());
	}

	@Test
	public void createDictionaryFromDefaultStream() {
		assertThat(map.get("краснеющий").length, is(2)); // 2 разных падежа
		assertThat(map.get("дорога").length, is(2));
		assertThat(map.get("клавиатура").length, is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		assertThat(map.get("фентифлюшка").length, is(0));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		assertThat(map.get("человек").length, is(2)); // два падежа(им и рд)
		assertThat(map.get("люди").length, is(1));
		assertThat(map.get("ребёнок").length, is(1));
		assertThat(map.get("дети").length, is(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() throws IOException {
		assertThat(map.get("super#starnge@string").length, is(0));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() throws IOException {
		assertThat(collectLemmas(map.get("дорога")), hasItems("дорога", "дорогой"));
		assertThat(collectLemmas(map.get("черномырдину")), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(collectLemmas(map.get("замок")), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(map.get("и").length, is(2));
	}


	@Test
	public void regression2() throws IOException {
		assertThat(collectLemmas(map.get("придет")), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() throws IOException {
		Set<String> norms = collectLemmas(map.get("люди"));
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

}