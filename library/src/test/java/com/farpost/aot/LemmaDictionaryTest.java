package com.farpost.aot;

import com.farpost.aot.data.LemmaInfo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LemmaDictionaryTest {

	private final LemmaDictionary d = new LemmaDictionary();

	public LemmaDictionaryTest() throws IOException {
	}


	@Test
	public void dictionaryShouldBeAbleToFindExistentWords() throws IOException {
		assertThat(d.preLookup("краснеющий"), hasSize(1));
		assertThat(d.preLookup("дорога"), hasSize(2));
		assertThat(d.preLookup("клавиатура"), hasSize(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		assertThat(d.preLookup("фентифлюшка"), hasSize(0));
	}

	@Test
	public void lowerCaseWorkingCorrectly() {
		List<String> lemmas = d
			.preLookup("Германия")
			.stream()
			.map(LemmaInfo::getLemma)
			.collect(toList());

		assertThat(lemmas, containsInAnyOrder("германия", "германий"));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		assertThat(d.preLookup("человек"), hasSize(1));
		assertThat(d.preLookup("люди"), hasSize(1));
		assertThat(d.preLookup("ребёнок"), hasSize(1));
		assertThat(d.preLookup("дети"), hasSize(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() throws IOException {
		assertThat(d.preLookup("super#starnge@string"), hasSize(0));
	}


	private static List<String> collectNorms(final List<LemmaInfo> l) {
		return l.stream().map(LemmaInfo::getLemma).collect(toList());
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() throws IOException {
		assertThat(collectNorms(d.preLookup("дорога")), hasItems("дорога", "дорогой"));
		assertThat(collectNorms(d.preLookup("черномырдину")), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(collectNorms(d.preLookup("замок")), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(d.preLookup("и"), hasSize(2));
	}


	@Test
	public void regression2() throws IOException {
		assertThat(collectNorms(d.preLookup("придет")), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() throws IOException {
		List<String> norms = collectNorms(d.preLookup("люди"));
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

}
