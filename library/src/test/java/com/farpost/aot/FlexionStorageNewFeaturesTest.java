package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.farpost.aot.GrammarStorageTest.collectGrammarInfo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class FlexionStorageNewFeaturesTest {

	private final FlexionStorage map;

	public FlexionStorageNewFeaturesTest() throws IOException {
		map = new FlexionStorage();
	}

	@Test
	public void grammarInfoIsCorrect1() {
		final List<List<GrammarInfo>> infoset = collectGrammarInfo(map.search("течь"));
		assertThat(infoset, hasSize(3));

		// TODO
		/*assertThat(infoset.get(0), Matchers.containsInAnyOrder(
			GrammarInfo.Infinitive,
			GrammarInfo.ActiveVoice));

		assertThat(infoset.get(1),
			Matchers.containsInAnyOrder(
				GrammarInfo.Noun,
				GrammarInfo.Nominative,
				GrammarInfo.Female,
				GrammarInfo.Singular));

		assertThat(infoset.get(2),
			Matchers.containsInAnyOrder(
				GrammarInfo.Noun,
				GrammarInfo.Accusative,
				GrammarInfo.Singular,
				GrammarInfo.Female));*/
	}

	@Test
	public void grammarInfoIsCorrect2() {
		final List<List<GrammarInfo>> infoset = collectGrammarInfo(map.search("дорога"));
		assertThat(infoset, hasSize(2));

		assertThat(infoset.get(1),
			Matchers.containsInAnyOrder(
				GrammarInfo.Noun,
				GrammarInfo.Nominative,
				GrammarInfo.Female,
				GrammarInfo.Singular));

		assertThat(infoset.get(0), Matchers.containsInAnyOrder(
			GrammarInfo.Singular,
			GrammarInfo.ShortAdjective,
			GrammarInfo.Female,
			GrammarInfo.Animated,
			GrammarInfo.Inanimate));
	}
}
