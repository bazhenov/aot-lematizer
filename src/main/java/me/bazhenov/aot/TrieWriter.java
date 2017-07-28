package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import static me.bazhenov.aot.Utils.safeCharToByte;

class TrieWriter {

	ByteBuffer write(Trie<? extends Addressed<?>> trie) {
		TrieNode<? extends Addressed<?>> root = trie.getRoot();
		int size = calculateSize(root);
		ByteBuffer buffer = ByteBuffer.allocate(size);
		write(root, buffer);
		buffer.flip();
		return buffer;
	}

	private int write(TrieNode<? extends Addressed<?>> node, ByteBuffer buffer) {
		int address = buffer.position();
		int sz = node.getChildren().size();
		buffer.put(safeCast(sz));
		int value = 0;
		if (node.getValue() != null) {
			int addr = node.getValue().getAddress();
			if (addr <= 0) {
				throw new IllegalArgumentException("Illegal address");
			}
			value = addr;
		}
		buffer.putInt(value);

		Set<Character> characters = node.getChildren().keySet();
		checkUniqBytes(characters);

		for (Character children : characters) {
			buffer.put(safeCharToByte(children));
		}
		int refsPosition = buffer.position();
		buffer.position(buffer.position() + sz * 4);

		// POSITION safe line
		for (TrieNode<? extends Addressed<?>> children : node.getChildren().values()) {
			int addr = write(children, buffer);
			buffer.putInt(refsPosition, addr);
			refsPosition += 4;
		}
		return address;
	}

	private static void checkUniqBytes(Set<Character> characters) {
		Set<Byte> writtenBytes = new HashSet<>();
		for (Character children : characters) {
			if (!writtenBytes.add(safeCharToByte(children))) {
				throw new IllegalStateException("Duplicate byte/character found: " + children);
			}
		}
	}

	private static byte safeCast(int n) {
		if (n > 255) {
			throw new IllegalArgumentException();
		}
		return (byte) n;
	}

	private int calculateSize(TrieNode<?> node) {
		int result = 1 + 4 + 5 * (node.getChildren().size());
		for (TrieNode<?> children : node.getChildren().values()) {
			result += calculateSize(children);
		}
		return result;
	}
}
