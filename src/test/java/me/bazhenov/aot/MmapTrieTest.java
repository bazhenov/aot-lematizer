package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapTrieTest {

	@Test
	public void foo() {
		MmapTrie trie = prepareTrie(source -> {
			source.add("мама", 1);
			source.add("папа", 2);
			source.add("матрас", 3);
		});

		assertThat(trie.lookup("мама"), is(1));
		assertThat(trie.lookup("папа"), is(2));
		assertThat(trie.lookup("матрас"), is(3));
		assertThat(trie.lookup("матрац"), is(0));
		assertThat(trie.lookup("цапля"), is(0));
	}

	@Test
	public void iterate() {
		MmapTrie trie = prepareTrie(source -> {
			source.add("", 1);
			source.add("м", 2);
			source.add("ма", 3);
			source.add("мама", 4);
		});

		MmapTrie.State state = trie.init();
		assertThat(state.value(), is(1));

		assertThat(state.step(safeCastCharacter('м')), is(true));
		assertThat(state.value(), is(2));

		assertThat(state.step(safeCastCharacter('а')), is(true));
		assertThat(state.value(), is(3));

		assertThat(state.step(safeCastCharacter('м')), is(true));
		assertThat(state.value(), is(0));

		assertThat(state.step(safeCastCharacter('а')), is(true));
		assertThat(state.value(), is(4));

		// после ухода на несуществубщий символ, система остается в предыдущем состоянии
		assertThat(state.step(safeCastCharacter('н')), is(false));
		assertThat(state.value(), is(4));
	}

	private static MmapTrie prepareTrie(Consumer<Trie<Integer>> init) {
		Trie<Integer> source = new Trie<>();
		init.accept(source);

		TrieWriter writer = new TrieWriter();
		ByteBuffer buffer = writer.write(source);

		return new MmapTrie(buffer);
	}
}
