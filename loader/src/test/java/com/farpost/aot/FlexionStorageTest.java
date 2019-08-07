package com.farpost.aot;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionStorageTest {

	@Test
	public void loadingIsCorrect() throws IOException {
		var store = new FlexionStorage();

		final var lemm = store.getAllLemmas();
		assertThat(lemm.get(lemm.size() - 1), is(equalTo("яэль")));
		assertThat(lemm.get(0), is(equalTo("еж")));
		assertThat(lemm, hasSize(174628));

		final var flex = store.getAllFlexion();
		assertThat(flex, hasSize(5017012));
		assertThat(flex.get(0).source, equalTo(lemm.get(0)));
	}
}
