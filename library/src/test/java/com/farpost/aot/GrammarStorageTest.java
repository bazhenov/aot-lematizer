package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.storages.GrammarStorage;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GrammarStorageTest {

	private static Set<GrammarInfo> set(GrammarInfo[] arr) {
		return Arrays.stream(arr).collect(Collectors.toSet());
	}

	@Test
	public void grammarLinesOrderLikeInGramtabFileTxt() throws IOException {
		GrammarStorage store = new GrammarStorage();

		assertThat(set(store.get(0)),
			containsInAnyOrder(
				GrammarInfo.Noun,
				GrammarInfo.Nominative,
				GrammarInfo.Male,
				GrammarInfo.Singular
			)
		);

		assertThat(set(store.get(721)),
			containsInAnyOrder(
				GrammarInfo.Noun,
				GrammarInfo.Nominative,
				GrammarInfo.Male,
				GrammarInfo.Singular,
				GrammarInfo.Plural,
				GrammarInfo.Dative,
				GrammarInfo.Female,
				GrammarInfo.Genitive,
				GrammarInfo.NeuterGender,
				GrammarInfo.Accusative,
				GrammarInfo.Ablative,
				GrammarInfo.Prepositional
			)
		);
	}

}
