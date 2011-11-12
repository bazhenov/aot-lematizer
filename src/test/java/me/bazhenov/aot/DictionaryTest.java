package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class DictionaryTest {

	@Test
	public void dictionaryStem() throws IOException {
		Dictionary d = new Dictionary(new FileInputStream("mrd"), new FileInputStream("tab"));
		System.out.println(d.getWordNorm("люди"));
	}

	@Test
	public void buildDictionarySmall() throws IOException, InterruptedException {
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long before = rt.totalMemory() - rt.freeMemory();
		Dictionary d = new Dictionary(new FileInputStream("mrd"), new FileInputStream("tab"));
		long after = rt.totalMemory() - rt.freeMemory();
		//System.out.println((after - before) / 1024 / 1024);

		System.out.println(d.getWordNorm("железными"));
		System.out.println(d.getWordNorm("керосина"));
		System.out.println(d.getWordNorm("красных"));
	}

	@Test
	public void readDictionary() throws IOException, InterruptedException, ClassNotFoundException {
		System.gc();
		long before = Runtime.getRuntime().freeMemory();

		InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream("tree")));
		DataInputStream is = new DataInputStream(in);
		TernarySearchTree tree = TernarySearchTree.readFrom(is);
		is.close();
		in.close();

		System.gc();
		long after = Runtime.getRuntime().freeMemory();
		System.out.print(((after - before) / 1024 / 1024) + "M");

		assertThat(tree.get("автомобиль"), greaterThan(0));
	}
}
