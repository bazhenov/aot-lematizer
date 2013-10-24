package me.bazhenov.aot;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.getFirst;
import static me.bazhenov.aot.Lemma.retireveWord;
import static me.bazhenov.aot.PartOfSpeech.Noun;
import static me.bazhenov.aot.TernaryTreeDictionary.loadDictionary;
import static me.bazhenov.aot.TernaryTreeDictionary.mergeIntersect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TernaryTreeDictionaryTest {

	private TernaryTreeDictionary dictionary;

	@BeforeClass
	public void setUp() throws Exception {
		dictionary = loadDictionary();
	}

	@Test
	public void lookup() throws IOException, InterruptedException {
		Set<Lemma> lemmas = dictionary.lookupWord("люди");
		Lemma l = getFirst(lemmas, null);
		assertThat(l.getPosTag(), is(Noun));
		Set<String> derivations = l.derivate("ед", "вн");
		assertThat(derivations, hasSize(1));
		assertThat(derivations, hasItem("человека"));
	}

	@Test
	public void prefixesShouldBeResolved() {
		List<String> lemmas = from(dictionary.lookupWord("полезай")).transform(retireveWord).toList();
		assertThat(lemmas, hasItem("лезть"));
	}

	@Test
	public void intersect() {
		IntArrayList a = new IntArrayList(new int[]{1, 2, 4, 6});
		IntArrayList b = new IntArrayList(new int[]{0, 2, 5, 6});

		IntArrayList result = mergeIntersect(a, b);
		assertThat(result.toIntArray(), is(new int[]{2, 6}));
	}
}

