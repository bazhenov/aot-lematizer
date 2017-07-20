package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapTrieTest {

	@Test
	public void foo() {
		Trie<Integer> source = new Trie<>();
		source.add("мама", 1);
		source.add("папа", 2);
		source.add("матрас", 3);

		TrieWriter writer = new TrieWriter();
		ByteBuffer buffer = writer.write(source);

		buffer.flip();
		MmapTrie trie = new MmapTrie(buffer);
		assertThat(trie.lookup("мама"), is(1));
		assertThat(trie.lookup("папа"), is(2));
		assertThat(trie.lookup("матрас"), is(3));
		assertThat(trie.lookup("матрац"), is(0));
		assertThat(trie.lookup("цапля"), is(0));
	}
}
