package me.bazhenov.aot;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LemmaRepositoryTest {

	private LemmaRepository lemmaRepository;

	private static Set<String> setFromElems(String... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

	@BeforeMethod
	public void setUp() throws Exception {
		lemmaRepository = new LemmaRepository();
	}

	@Test
	public void testInsertAndGet() throws Exception {
		Lemma lemma1 = new Lemma("кур", Collections.singletonList(new Flexion("ица", "", "Г Г Г Г Г")));
		Lemma lemma2 = new Lemma("крас", Collections.singletonList(new Flexion("ивая", "", "Г Г Г Г Г")));
		Lemma lemma3 = new Lemma("клав", Collections.singletonList(new Flexion("иша", "", "Г Г Г Г Г")));
		lemmaRepository.insert(lemma1);
		lemmaRepository.insert(lemma2);
		lemmaRepository.insert(lemma3);

		Set<Lemma> lemmas = lemmaRepository.findByBaseIn(setFromElems("кур", "клав", "мир"));
		assertThat(lemmas, hasSize(2));
		assertThat(lemmas, containsInAnyOrder(lemma1, lemma3));
	}

	@Test
	public void testFindInEmptyRepository() throws Exception {
		assertThat(lemmaRepository.findByBaseIn(setFromElems("кур")), empty());
	}

	@Test
	public void testFindByWrongBase() throws Exception {
		Lemma lemma2 = new Lemma("крас", Collections.singletonList(new Flexion("ивая", "", "Г Г Г Г Г")));
		Lemma lemma3 = new Lemma("клав", Collections.singletonList(new Flexion("иша", "", "Г Г Г Г Г")));
		lemmaRepository.insert(lemma2);
		lemmaRepository.insert(lemma3);

		Set<Lemma> lemmas = lemmaRepository.findByBaseIn(setFromElems("кур"));
		assertThat(lemmas, empty());
	}
}