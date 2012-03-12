package me.bazhenov.aot;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DictionaryTest {

	private Dictionary dictionary;

	@BeforeClass
	protected void setUp() throws Exception {
		dictionary = new Dictionary(new File("target/aot.dict"));
	}

	@Test
	public void dictionaryStem() throws IOException {
		assertThat(lookup("люди"), equalTo("человек"));
		assertThat(lookup("железными"), equalTo("железный"));
		assertThat(lookup("керосина"), equalTo("керосин"));
	}

	private String lookup(String word) {
		return dictionary.getWordNorm(word).get(0).getWord();
	}
}
