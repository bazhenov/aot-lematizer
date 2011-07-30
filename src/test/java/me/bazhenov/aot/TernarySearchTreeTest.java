package me.bazhenov.aot;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TernarySearchTreeTest {

	@Test
	public void test() {
		TernarySearchTree tree = new TernarySearchTree();
		tree.insert("AB");
		tree.insert("ABBA");
		tree.insert("ABCD");
		tree.insert("BCD");

		assertThat(tree.containsKey("AB"), is(true));
		assertThat(tree.containsKey("ABBA"), is(true));
		assertThat(tree.containsKey("ABCD"), is(true));
		assertThat(tree.containsKey("BCD"), is(true));
	}
}
