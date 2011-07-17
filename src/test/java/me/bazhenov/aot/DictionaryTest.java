package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class DictionaryTest {

	@Test
	public void dictionaryStem() throws IOException {
		Dictionary d = new Dictionary(new FileInputStream("mrd"), new FileInputStream("tab"));
		System.out.println(d.getWordNorm("люди"));
	}
}
