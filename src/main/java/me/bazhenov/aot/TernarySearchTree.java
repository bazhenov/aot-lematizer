package me.bazhenov.aot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final public class TernarySearchTree {

	private final List<TNode> buffer;

	public TernarySearchTree() {
		buffer = new ArrayList<TNode>();
		buffer.add(new TNode('*', 0, buffer));
	}

	public TernarySearchTree(List<TNode> buffer) {
		this.buffer = buffer;
	}

	public void insert(String s, int value) {
		if (s == null || s.equals("")) {
			throw new IllegalArgumentException();
		}

		root().insert(s, value);
	}

	private TNode root() {
		return buffer.get(0);
	}

	public int get(String key) {
		if (key == null || key.equals("")) throw new IllegalArgumentException();

		int pos = 0;
		TNode node = root();
		while (node != null) {
			char c = key.charAt(pos);
			if (c < node.character) {
				node = buffer.get(node.left);
			} else if (c > node.character) {
				node = buffer.get(node.right);
			} else {
				if (++pos == key.length()) {
					return node.value;
				}
				node = buffer.get(node.center);
			}
		}

		return 0;
	}

	public void writeTo(DataOutputStream os) throws IOException {
		os.writeInt(buffer.size());
		for (TNode node : buffer) {
			node.writeTo(os);
		}
	}

	public static TernarySearchTree readFrom(DataInputStream is) throws IOException {
		int size = is.readInt();
		List<TNode> buffer = new ArrayList<TNode>();
		for (int i=0; i<size; i++) {
			TNode.readFrom(is, buffer);
		}
		return new TernarySearchTree(buffer);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TernarySearchTree that = (TernarySearchTree) o;

		if (buffer != null ? !buffer.equals(that.buffer) : that.buffer != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return buffer != null ? buffer.hashCode() : 0;
	}
}

final class TNode {

	final char character;
	int left, right, center;
	int value;
	private final List<TNode> buffer;

	public TNode(char character, int value, List<TNode> buffer) {
		this.character = character;
		this.value = value;
		this.buffer = buffer;
	}

	public TNode(char character, int value, List<TNode> buffer, int left, int center, int right) {
		this.character = character;
		this.value = value;
		this.buffer = buffer;
		this.left = left;
		this.center = center;
		this.right = right;
	}

	void insert(String key, int value) {
		if (key.isEmpty()) {
			return;
		}
		char c = key.charAt(0);
		String substr = key.substring(1);

		if (c < character) {
			if (left == 0) {
				left = createNode(buffer, c, substr.isEmpty() ? value : 0);
			}
			buffer.get(left).insert(key, value);

		} else if (c > character) {
			if (right == 0) {
				right = createNode(buffer, c, substr.isEmpty() ? value : 0);
			}
			buffer.get(right).insert(key, value);
		} else {
			if (substr.isEmpty()) {
				this.value = value;
			} else {
				if (center == 0) {
					center = createNode(buffer, substr.charAt(0), 0);
				}
				buffer.get(center).insert(substr, value);
			}
		}
	}

	private int createNode(List<TNode> buffer, char ch, int value) {
		int newIndex = buffer.size();
		TNode node = new TNode(ch, value, buffer);
		buffer.add(node);
		return newIndex;
	}

	void writeTo(DataOutputStream os) throws IOException {
		os.writeChar(character);
		os.writeInt(left);
		os.writeInt(center);
		os.writeInt(right);
		os.writeInt(value);
	}

	static void readFrom(DataInputStream is, List<TNode> buffer) throws IOException {
		char character = is.readChar();
		int left = is.readInt();
		int center = is.readInt();
		int right = is.readInt();
		int value = is.readInt();
		TNode node = new TNode(character, value, buffer, left, center, right);
		buffer.add(node);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TNode tNode = (TNode) o;

		if (center != tNode.center) return false;
		if (character != tNode.character) return false;
		if (left != tNode.left) return false;
		if (right != tNode.right) return false;
		if (value != tNode.value) return false;

		return true;
	}
}
