package me.bazhenov.aot;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TrieTest {

	private Trie<Long> trie;

	@BeforeMethod
	public void setUp() {
		trie = new Trie<Long>();
	}

	@Test
	public void testTrieAddSearch() {
		trie.add("foo", 1L);
		trie.add("far", 2L);
		trie.add("bar", 3L);
		trie.add("foofoo", 5L);

		assertThat(trie.search("foo"), equalTo(1L));
		assertThat(trie.search("foofoo"), equalTo(5L));
		assertThat(trie.search("far"), equalTo(2L));
		assertThat(trie.search("bar"), equalTo(3L));

		assertThat(trie.search("boo"), nullValue());
		assertThat(trie.search("foof"), nullValue());
		assertThat(trie.search("foofo"), nullValue());
	}

	@Test
	public void trieCouldBeSerialized() throws IOException, ClassNotFoundException {
		trie.add("foo", 1L);
		trie.add("bar", 2L);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);

		oos.writeObject(trie);
		oos.close();

		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(is);

		Trie<Long> trieCopy = (Trie<Long>)ois.readObject();
		assertThat(trieCopy.search("foo"), is(1L));
		assertThat(trieCopy.search("bar"), is(2L));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void exceptionShouldBeGeneratedOnDuplicate() {
		trie.add("foo", 1L);
		trie.add("foo", 1L);
	}

	@Test
	public void replace() {
		trie.add("foo", 1L);
		trie.replace("foo", 2L);

		assertThat(trie.search("foo"), is(2L));
	}
}
