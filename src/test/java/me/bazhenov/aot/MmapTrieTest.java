package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static me.bazhenov.aot.Utils.safeCharToByte;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmapTrieTest {

	@Test
	public void foo() {
		MmapTrie trie = prepareTrie(source -> {
			source.add("мама", new Addressed<>(1));
			source.add("папа", new Addressed<>(2));
			source.add("матрас", new Addressed<>(3));
		});

		assertThat(trie.lookup("папа"), is(2));
		assertThat(trie.lookup("мама"), is(1));
		assertThat(trie.lookup("матрас"), is(3));
		assertThat(trie.lookup("матрац"), is(0));
		assertThat(trie.lookup("цапля"), is(0));
	}

	@Test
	public void iterate() {
		MmapTrie trie = prepareTrie(source -> {
			source.add("", new Addressed<>(1));
			source.add("м", new Addressed<>(2));
			source.add("ма", new Addressed<>(3));
			source.add("мама", new Addressed<>(4));
		});

		MmapTrie.State state = trie.init();
		assertThat(state.value(), is(1));

		assertThat(state.step(safeCharToByte('м')), is(true));
		assertThat(state.value(), is(2));

		assertThat(state.step(safeCharToByte('а')), is(true));
		assertThat(state.value(), is(3));

		assertThat(state.step(safeCharToByte('м')), is(true));
		assertThat(state.value(), is(0));

		assertThat(state.step(safeCharToByte('а')), is(true));
		assertThat(state.value(), is(4));

		// после ухода на несуществубщий символ, система остается в предыдущем состоянии
		assertThat(state.step(safeCharToByte('н')), is(false));
		assertThat(state.value(), is(4));
	}

	private static MmapTrie prepareTrie(Consumer<Trie<Addressed<?>>> init) {
		Trie<Addressed<?>> source = new Trie<>();
		init.accept(source);

		TrieWriter writer = new TrieWriter();
		ByteBuffer buffer = writer.write(source);

		return new MmapTrie(buffer);
	}
}
