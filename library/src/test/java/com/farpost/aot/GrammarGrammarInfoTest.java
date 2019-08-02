package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GrammarGrammarInfoTest {

	@Test
	public void toStringMethodTest() {
		assertThat(GrammarInfo.Archaism.toString(), is(equalTo("арх")));
	}
}
