package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static me.bazhenov.aot.Utils.safeByteToChar;
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

		if (!node.getChildren().isEmpty()) {
			Set<Character> characters = node.getChildren().keySet();

			SortedSet<Byte> bytes = new TreeSet<>();
			for (Character c : characters) {
				if (!bytes.add(safeCharToByte(c))) {
					throw new IllegalStateException("Duplicate byte/character found: " + c);
				}
			}
			if (bytes.first() > bytes.last())
				throw new IllegalStateException("Invalid order");

			for (byte b : bytes) {
				buffer.put(b);
			}
			int refsPosition = buffer.position();
			buffer.position(buffer.position() + sz * 4);

			// POSITION safe line
			for (byte b : bytes) {
				int addr = write(node.getChildren().get(safeByteToChar(b)), buffer);
				buffer.putInt(refsPosition, addr);
				refsPosition += 4;
			}
		}
		return address;
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
