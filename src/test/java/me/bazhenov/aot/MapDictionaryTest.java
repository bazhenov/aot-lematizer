package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.getFirst;
import static java.util.stream.Collectors.toList;
import static me.bazhenov.aot.MapDictionary.loadDictionary;
import static me.bazhenov.aot.PartOfSpeech.Adjective;
import static me.bazhenov.aot.PartOfSpeech.Noun;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MapDictionaryTest {

	private MapDictionary dict = loadDictionary();

	@Test
	public void testLookupSimpleWord() {
		Set<Lemma> lemmas = dict.lookupWord("курица");
		assertThat(lemmas, hasSize(1));
	}

	@Test
	public void testNotExistingWord() {
		Set<Lemma> lemmas = dict.lookupWord("серобуромалиновый");
		assertThat(lemmas, empty());
	}

	@Test
	public void testLookupWrongCaseWord() {
		Set<Lemma> lemmas = dict.lookupWord("КУриЦА");
		assertThat(lemmas, hasSize(1));
	}

	@Test
	public void testLookupWithInsensitiveLetters() throws Exception {
		Set<Lemma> lemmas = dict.lookupWord("зелёный");
		Lemma l = getFirst(lemmas, null);
		assertThat(l.getPosTag(), is(Adjective));
		Set<String> derivations = l.derivate("ед", "вн");
		assertThat(derivations, hasItem("зеленого"));
	}

	@Test
	public void testLookupWordWithDifferentBase() throws Exception {
		Set<Lemma> lemmas = dict.lookupWord("люди");
		Lemma l = getFirst(lemmas, null);
		assertThat(l.getPosTag(), is(Noun));
		Set<String> derivations = l.derivate("ед", "вн");
		assertThat(derivations, hasSize(1));
		assertThat(derivations, hasItem("человека"));
	}

	@Test
	public void testPrefixesShouldBeResolved() {
		List<String> lemmas = dict.lookupWord("полезай").stream()
			.map(Lemma::getWord)
			.collect(toList());
		assertThat(lemmas, hasItem("лезть"));

		lemmas = dict.lookupWord("продам").stream()
			.map(Lemma::getWord)
			.collect(toList());
		assertThat(lemmas, hasItem("продать"));
	}

	@Test
	public void testReload() throws Exception {
		Set<Lemma> lemmas = dict.lookupWord("серобуромалиновый");
		assertThat(lemmas, empty());
		dict.reload(getClass().getResourceAsStream("/mrd_dummy"));
		lemmas = dict.lookupWord("серобуромалиновый");
		assertThat(lemmas, hasSize(1));

		dict.reload(getClass().getResourceAsStream("/mrd"));
	}

	@Test
	public void testReloadNotChangedAfterException() throws Exception {
		try {
			dict.reload(getClass().getResourceAsStream("/iamnotexists"));
		} catch (NullPointerException ignored) {
		}
		Set<Lemma> lemmas = dict.lookupWord("курица");
		assertThat(lemmas, hasSize(1));
	}

	@Test
	public void testCustomLoad() throws Exception {
		MapDictionary dict = MapDictionary.loadDictionary(getClass().getResourceAsStream("/mrd_dummy"));
		Set<Lemma> lemmas = dict.lookupWord("серобуромалиновый");
		assertThat(lemmas, hasSize(1));
	}

	@Test
	public void testLookupPreffixAndEndingsFromDifferentFlexion() throws Exception {
		Set<Lemma> lemmas = dict.lookupWord("поклейка");
		assertThat(lemmas, empty());
	}
}
