package me.bazhenov.aot;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

import static com.google.common.collect.Iterables.getFirst;
import static me.bazhenov.aot.PartOfSpeech.Noun;
import static me.bazhenov.aot.TernaryTreeDictionary.mergeIntersect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TernaryTreeDictionaryTest {

	@Test
	public void lookup() throws IOException, InterruptedException {
		Dictionary d = TernaryTreeDictionary.loadDictionary();
		Set<Lemma> lemmas = d.lookupWord("люди");
		Lemma l = getFirst(lemmas, null);
		assertThat(l.getPosTag(), is(Noun));
		Set<String> derivations = l.derivate("ед", "вн");
		assertThat(derivations, hasSize(1));
		assertThat(derivations, hasItem("человека"));

	}

	@Test
	public void intersect() {
		IntArrayList a = new IntArrayList(new int[]{1, 2, 4, 6});
		IntArrayList b = new IntArrayList(new int[]{0, 2, 5, 6});

		IntArrayList result = mergeIntersect(a, b);
		assertThat(result.toIntArray(), is(new int[]{2, 6}));
	}
}

