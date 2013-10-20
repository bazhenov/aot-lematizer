package me.bazhenov.aot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

final public class TernarySearchTree<T> {

	private final List<TNode<T>> buffer;

	public TernarySearchTree() {
		buffer = newArrayList();
		buffer.add(new TNode<T>('*', null, buffer));
	}

	public TernarySearchTree(List<TNode<T>> buffer) {
		this.buffer = buffer;
	}

	public void insert(String s, T value) {
		root().insert(s, value);
	}

	private TNode<T> root() {
		return buffer.get(0);
	}

	public T get(String key) {
		int pos = 0;
		TNode<T> node = root();
		if (key.isEmpty()) {
			return node.value;
		}
		while (node != null) {
			char c = key.charAt(pos);
			if (c < node.character) {
				node = node.left > 0 ? buffer.get(node.left) : null;
			} else if (c > node.character) {
				node = node.right > 0 ? buffer.get(node.right) : null;
			} else {
				if (++pos >= key.length()) {
					return node.value;
				}
				node = node.center > 0 ? buffer.get(node.center) : null;
			}
		}

		return null;
	}

	public Map<String, T> findAllInPath(String prefix) {
		int pos = 0;
		TNode<T> node = root();
		Map<String, T> result = newHashMap();
		if (node.value != null) {
			result.put("", node.value);
		}
		while (node != null && pos < prefix.length()) {
			char c = prefix.charAt(pos);
			if (c < node.character) {
				node = node.left > 0 ? buffer.get(node.left) : null;
			} else if (c > node.character) {
				node = node.right > 0 ? buffer.get(node.right) : null;
			} else {
				pos++;
				if (node.value != null) {
					result.put(prefix.substring(0, pos), node.value);
				}
				node = node.center > 0 ? buffer.get(node.center) : null;
			}
		}

		return result;
	}

	public void writeTo(DataOutputStream os) throws IOException {
		os.writeInt(buffer.size());
		for (TNode<T> node : buffer) {
			node.writeTo(os);
		}
	}

	public static <T> TernarySearchTree<T> readFrom(DataInputStream is) throws IOException {
		int size = is.readInt();
		List<TNode<T>> buffer = new ArrayList<TNode<T>>();
		for (int i = 0; i < size; i++) {
			TNode.readFrom(is, buffer);
		}
		return new TernarySearchTree<T>(buffer);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TernarySearchTree<T> that = (TernarySearchTree<T>) o;

		if (buffer != null ? !buffer.equals(that.buffer) : that.buffer != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return buffer != null ? buffer.hashCode() : 0;
	}
}

final class TNode<T> {

	final char character;
	int left, right, center;
	T value;
	private final List<TNode<T>> buffer;

	public TNode(char character, T value, List<TNode<T>> buffer) {
		this.character = character;
		this.value = value;
		this.buffer = buffer;
	}

	public TNode(char character, T value, List<TNode<T>> buffer, int left, int center, int right) {
		this.character = character;
		this.value = value;
		this.buffer = buffer;
		this.left = left;
		this.center = center;
		this.right = right;
	}

	void insert(String key, T value) {
		if (key.isEmpty()) {
			this.value = value;
			return;
		}
		char c = key.charAt(0);
		String substr = key.substring(1);

		if (c < character) {
			if (left == 0) {
				left = createNode(buffer, c, substr.isEmpty() ? value : null);
			}
			buffer.get(left).insert(key, value);

		} else if (c > character) {
			if (right == 0) {
				right = createNode(buffer, c, substr.isEmpty() ? value : null);
			}
			buffer.get(right).insert(key, value);
		} else {
			if (substr.isEmpty()) {
				this.value = value;
			} else {
				if (center == 0) {
					center = createNode(buffer, substr.charAt(0), null);
				}
				buffer.get(center).insert(substr, value);
			}
		}
	}

	private int createNode(List<TNode<T>> buffer, char ch, T value) {
		int newIndex = buffer.size();
		TNode<T> node = new TNode<T>(ch, value, buffer);
		buffer.add(node);
		return newIndex;
	}

	void writeTo(DataOutputStream os) throws IOException {
		os.writeChar(character);
		os.writeInt(left);
		os.writeInt(center);
		os.writeInt(right);
	}

	static <T> void readFrom(DataInputStream is, List<TNode<T>> buffer) throws IOException {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TNode<T> tNode = (TNode<T>) o;

		if (center != tNode.center) return false;
		if (character != tNode.character) return false;
		if (left != tNode.left) return false;
		if (right != tNode.right) return false;
		if (value != tNode.value) return false;

		return true;
	}
}
