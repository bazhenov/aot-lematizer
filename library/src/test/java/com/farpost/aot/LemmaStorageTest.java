package com.farpost.aot;

import com.farpost.aot.storages.LemmaStorage;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LemmaStorageTest {
	@Test public void readedLemmasOrderLikeInMrdTxt() throws IOException {
		LemmaStorage store = new LemmaStorage();

		assertThat(store.get(0), is(equalTo("еж")));

		assertThat(store.get(171_364), is(equalTo("яэль")));
	}
}
