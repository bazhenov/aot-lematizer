package me.bazhenov.aot;

import org.testng.annotations.Test;

import java.io.*;

import static me.bazhenov.aot.TernarySearchTree.readFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TernarySearchTreeTest {

	@Test
	public void test() {
		TernarySearchTree tree = new TernarySearchTree();
		tree.insert("AB", 1);
		tree.insert("ABBA", 2);
		tree.insert("ABCD", 3);
		tree.insert("BCD", 4);

		assertThat(tree.get("AB"), equalTo(1));
		assertThat(tree.get("ABB"), equalTo(0));
		assertThat(tree.get("ABBA"), equalTo(2));
		assertThat(tree.get("ABCD"), equalTo(3));
		assertThat(tree.get("BCD"), equalTo(4));
	}

	@Test
	public void testSerialization() throws IOException {
		TernarySearchTree tree = new TernarySearchTree();
		tree.insert("AB", 1);
		tree.insert("ABBA", 2);
		tree.insert("ABCD", 3);
		tree.insert("BCD", 4);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(os);
		tree.writeTo(dataOutputStream);
		dataOutputStream.close();

		TernarySearchTree treeCopy = readFrom(new DataInputStream(new ByteArrayInputStream(os.toByteArray())));
		assertThat(treeCopy, equalTo(tree));
	}
}
