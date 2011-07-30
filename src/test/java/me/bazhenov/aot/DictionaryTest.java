package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DictionaryTest {

	@Test
	public void dictionaryStem() throws IOException {
		Dictionary d = new Dictionary(new FileInputStream("mrd"), new FileInputStream("tab"));
		System.out.println(d.getWordNorm("люди"));
	}

	@Test
	public void buildDictionary() throws IOException {
		Dictionary.buildTrie(new FileInputStream("mrd"), new FileInputStream("tab"), new FileOutputStream("trie"));
	}
}
