/*package com.farpost.aot;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HashDictionaryTest {

	private final HashDictionary d = new HashDictionary();

	public HashDictionaryTest() throws IOException {
	}


	@Test
	public void dictionaryShouldBeAbleToFindExistentWords() throws IOException {
		assertThat(d.lookup("краснеющий"), hasSize(1));
		assertThat(d.lookup("дорога"), hasSize(2));
		assertThat(d.lookup("клавиатура"), hasSize(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		assertThat(d.lookup("фентифлюшка"), hasSize(0));
	}

	@Test
	public void lowerCaseWorkingCorrectly() {
		List<String> lemmas = d
			.lookup("Германия")
			.stream()
			.map(x -> x.get(0).getString())
			.collect(toList());

		assertThat(lemmas, containsInAnyOrder("германия", "германий"));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		assertThat(d.lookup("человек"), hasSize(1));
		assertThat(d.lookup("люди"), hasSize(1));
		assertThat(d.lookup("ребёнок"), hasSize(1));
		assertThat(d.lookup("дети"), hasSize(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() throws IOException {
		assertThat(d.lookup("super#starnge@string"), hasSize(0));
	}


	private static List<String> collectNorms(final List<List<Flexion>> l) {
		return l.stream().map(x -> x.get(0).getString()).collect(toList());
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() throws IOException {
		assertThat(collectNorms(d.lookup("дорога")), hasItems("дорога", "дорогой"));
		assertThat(collectNorms(d.lookup("черномырдину")), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(collectNorms(d.lookup("замок")), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(d.lookup("и"), hasSize(2));
	}


	@Test
	public void regression2() throws IOException {
		assertThat(collectNorms(d.lookup("придет")), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() throws IOException {
		List<String> norms = collectNorms(d.lookup("люди"));
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

}*/
