package com.farpost.aot;

import com.farpost.aot.data.Flexion;
import com.farpost.aot.data.GrammarInfo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionStorageTest {

	private FlexionStorage map;

	public FlexionStorageTest() throws IOException {
		map = new FlexionStorage();
	}

	public static Set<String> collectLemmas(final Collection<Flexion> results) {
		return (results).stream().map(x -> x.lemma).collect(Collectors.toSet());
	}

	public static List<Set<GrammarInfo>> collectGrammarInfo(final Collection<Flexion> results) {
		return results.stream()
			.map(x -> new HashSet<>(x.grammarInfo))
			.collect(Collectors.toList());
	}

	@Test
	public void createDictionaryFromDefaultStream() {
		assertThat(map.get("краснеющий").size(), is(2)); // 2 разных падежа
		assertThat(map.get("дорога").size(), is(2));
		assertThat(map.get("клавиатура").size(), is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() {
		assertThat(map.get("фентифлюшка").size(), is(0));
	}

	@Test
	public void testEmptyWordBases() {
		assertThat(map.get("человек").size(), is(2)); // два падежа(им и рд)
		assertThat(map.get("люди").size(), is(1));
		assertThat(map.get("ребёнок").size(), is(1));
		assertThat(map.get("дети").size(), is(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() {
		assertThat(map.get("super#starnge@string").size(), is(0));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() {
		assertThat(collectLemmas(map.get("дорога")), hasItems("дорога", "дорогой"));
		assertThat(collectLemmas(map.get("черномырдину")), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(collectLemmas(map.get("замок")), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(map.get("и").size(), is(2));
	}


	@Test
	public void regression2() {
		assertThat(collectLemmas(map.get("придет")), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() {
		Set<String> norms = collectLemmas(map.get("люди"));
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

}