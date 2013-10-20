package me.bazhenov.aot;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TernarySearchTreeTest {

	private TernarySearchTree<Integer> tree;

	@BeforeMethod
	public void setUp() throws Exception {
		tree = new TernarySearchTree<Integer>();
	}

	@Test
	public void test() {
		tree.insert("AB", 1);
		tree.insert("ABBA", 2);
		tree.insert("ABCD", 3);
		tree.insert("BCD", 4);
		tree.insert("", 8);

		assertThat(tree.get(""), equalTo(8));
		assertThat(tree.get("A"), nullValue());
		assertThat(tree.get("AB"), equalTo(1));
		assertThat(tree.get("ABB"), nullValue());
		assertThat(tree.get("ZFE"), nullValue());
		assertThat(tree.get("ABBA"), equalTo(2));
		assertThat(tree.get("ABBATTTA"), nullValue());
		assertThat(tree.get("ABCD"), equalTo(3));
		assertThat(tree.get("BCD"), equalTo(4));
	}

	@Test
	public void findAllInPath() {
		tree.insert("", 4);
		tree.insert("AB", 1);
		tree.insert("ABBA", 2);
		tree.insert("ABCD", 3);

		Map<String, Integer> result = tree.findAllInPath("ABBAT");
		assertThat(result.size(), is(3));
		assertThat(result, hasEntry("", 4));
		assertThat(result, hasEntry("AB", 1));
		assertThat(result, hasEntry("ABBA", 2));

		result = tree.findAllInPath("ABBA");
		assertThat(result.size(), is(3));
		assertThat(result, hasEntry("", 4));
		assertThat(result, hasEntry("AB", 1));
		assertThat(result, hasEntry("ABBA", 2));

		result = tree.findAllInPath("ABA");
		assertThat(result.size(), is(2));
		assertThat(result, hasEntry("", 4));
		assertThat(result, hasEntry("AB", 1));

		result = tree.findAllInPath("A");
		assertThat(result.size(), is(1));
		assertThat(result, hasEntry("", 4));
	}
}
