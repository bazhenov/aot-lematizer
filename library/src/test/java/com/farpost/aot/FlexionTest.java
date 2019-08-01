package com.farpost.aot;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionTest {
	@Test
	public void toStringMethodTest() {
		assertThat(
			new Flexion("яблоко", new GrammarInfo[]{GrammarInfo.Noun}).toString(),
			is(equalTo("яблоко(С)")));
	}
}
