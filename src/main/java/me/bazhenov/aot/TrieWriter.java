package me.bazhenov.aot;

import java.nio.ByteBuffer;

import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;

public class TrieWriter {

	public ByteBuffer write(Trie<Integer> trie) {
		TrieNode<Integer> root = trie.getRoot();
		int size = calculateSize(root);
		ByteBuffer buffer = ByteBuffer.allocate(size);
		write(root, buffer);
		return buffer;
	}

	private int write(TrieNode<Integer> node, ByteBuffer buffer) {
		int address = buffer.position();
		int sz = node.getChildren().size();
		buffer.put(safeCast(sz));
		int value = node.getValue() == null
			? 0
			: node.getValue();
		buffer.putInt(value);
		for (Character children : node.getChildren().keySet()) {
			buffer.put(safeCastCharacter(children));
		}
		int refsPosition = buffer.position();
		buffer.position(buffer.position() + sz * 4);

		// POSITION safe line
		for (TrieNode<Integer> children : node.getChildren().values()) {
			int addr = write(children, buffer);
			buffer.putInt(refsPosition, addr);
			refsPosition += 4;
		}
		return address;
	}

	private static byte safeCast(int n) {
		if (n > 255) {
			throw new IllegalArgumentException();
		}
		return (byte) n;
	}

	private int calculateSize(TrieNode<Integer> node) {
		int result = 1 + 4 + 5 * (node.getChildren().size());
		for (TrieNode<Integer> children : node.getChildren().values()) {
			result += calculateSize(children);
		}
		return result;
	}
}
