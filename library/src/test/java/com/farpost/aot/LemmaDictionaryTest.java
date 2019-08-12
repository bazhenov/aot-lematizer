package com.farpost.aot;

import com.farpost.aot.data.LemmaInfo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class LemmaDictionaryTest {

	private final LemmaDictionary d = new LemmaDictionary();

	public LemmaDictionaryTest() throws IOException {
	}


	@Test
	public void dictionaryShouldBeAbleToFindExistentWords() throws IOException {
		assertThat(d.lookup("краснеющий").size(), is(1));
		assertThat(d.lookup("дорога").size(), is(2));
		assertThat(d.lookup("клавиатура").size(), is(1));
	}

	@Test
	public void dictionaryShouldNotFindNotRealWords() throws IOException {
		assertThat(d.lookup("фентифлюшка").size(), is(0));
	}

	@Test
	public void testEmptyWordBases() throws IOException {
		assertThat(d.lookup("человек").size(), is(1));
		assertThat(d.lookup("люди").size(), is(1));
		assertThat(d.lookup("ребёнок").size(), is(1));
		assertThat(d.lookup("дети").size(), is(1));
	}

	@Test
	public void shouldNotThrowExceptionIfWordHasUnknownCharacter() throws IOException {
		assertThat(d.lookup("super#starnge@string").size(), is(0));
	}


	private static List<String> collectNorms(final List<LemmaInfo> l) {
		return l.stream().map(LemmaInfo::getLemma).collect(Collectors.toList());
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
		assertThat(d.lookup("и").size(), is(2));
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

}
