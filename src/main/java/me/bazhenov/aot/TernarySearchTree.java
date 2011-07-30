package me.bazhenov.aot;

final public class TernarySearchTree {

	private final TNode root = new TNode('*', false);

	public void insert(String s) {
		if (s == null || s.equals("")) {
			throw new IllegalArgumentException();
		}

		root.insert(s);
	}

	public boolean containsKey(String key) {
		if (key == null || key.equals("")) throw new IllegalArgumentException();

		int pos = 0;
		TNode node = this.root;
		while (node != null) {
			char c = key.charAt(pos);
			if (c < node.character) {
				node = node.left;
			} else if (c > node.character) {
				node = node.right;
			} else {
				if (++pos == key.length()) {
					return node.wordEnd;
				}
				node = node.center;
			}
		}

		return false;
	}
}

final class TNode {

	final char character;
	TNode left, center, right;
	boolean wordEnd;

	public TNode(char character, boolean wordEnd) {
		this.character = character;
		this.wordEnd = wordEnd;
	}

	void insert(String key) {
		if (key.isEmpty()) {
			return;
		}
		char c = key.charAt(0);
		String substr = key.substring(1);

		if (c < character) {
			if (left == null) {
				left = new TNode(c, substr.isEmpty());
			}
			left.insert(key);

		} else if (c > character) {
			if (right == null) {
				right = new TNode(c, substr.isEmpty());
			}
			right.insert(key);
		} else {
			if (substr.isEmpty()) {
				wordEnd = true;
			} else {
				if (center == null) {
					center = new TNode(substr.charAt(0), false);
				}
				center.insert(substr);
			}
		}
	}

	@Override
	public String toString() {
		return "TNode{" +
			"character=" + character +
			", left=" + left +
			", center=" + center +
			", right=" + right +
			", wordEnd=" + wordEnd +
			'}';
	}
}
