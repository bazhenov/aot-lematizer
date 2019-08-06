package com.farpost.aot;

import com.farpost.aot.data.FlexionInfo;
import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.data.LemmaInfo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionStorageTest {

	private FlexionStorage map;

	public FlexionStorageTest() throws IOException {
		map = new FlexionStorage();
	}

	public static List<String> collectLemmas(final Collection<LemmaInfo> results) {
		return (results).stream().map(LemmaInfo::getLemma).collect(Collectors.toList());
	}

	public static List<List<GrammarInfo>> collectGrammarInfo(final Collection<LemmaInfo> results) {

		final List<List<GrammarInfo>> res = new ArrayList<>();

		for (final LemmaInfo lemma : results) {
			for (final FlexionInfo flexion : lemma.getAllFlexions()) {
				res.add(flexion.getAllGrammarInfo());
			}
		}

		return res;
	}

	@Test
	public void createDictionaryFromDefaultStream() {
		assertThat(map.search("краснеющий").size(), is(1));
		assertThat(map.search("дорога").size(), is(2));
		assertThat(map.search("клавиатура").size(), is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() {
		assertThat(map.search("фентифлюшка").size(), is(0));
	}

	@Test
	public void testEmptyWordBases() {
		assertThat(map.search("человек").size(), is(1));
		assertThat(map.search("люди").size(), is(1));
		assertThat(map.search("ребёнок").size(), is(1));
		assertThat(map.search("дети").size(), is(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() {
		assertThat(map.search("super#starnge@string").size(), is(0));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNorms() {
		assertThat(collectLemmas(map.search("дорога")), hasItems("дорога", "дорогой"));
		assertThat(collectLemmas(map.search("черномырдину")), hasItems("черномырдин"));
	}

	@Test
	public void regression1() {
		assertThat(collectLemmas(map.search("замок")), hasItems("замок", "замокнуть"));
	}

	@Test
	public void regression3() {
		assertThat(map.search("и").size(), is(2)); //TODO ???
	}


	@Test
	public void regression2() {
		assertThat(collectLemmas(map.search("придет")), hasItems("прийти"));
	}

	@Test
	public void dictionaryShouldBeAbleToReturnWordNormsForEmptyBases() {
		List<String> norms = collectLemmas(map.search("люди"));
		assertThat(norms, hasSize(1));
		assertThat(norms, hasItems("человек"));
	}

}