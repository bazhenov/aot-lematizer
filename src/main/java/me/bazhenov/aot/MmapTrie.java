package me.bazhenov.aot;

import java.nio.ByteBuffer;

import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;

public class MmapTrie {

	private ByteBuffer buffer;

	public MmapTrie(ByteBuffer buffer) {
		if (buffer.position() != 0) {
			throw new IllegalArgumentException("Buffer should be positioned at start");
		}
		this.buffer = buffer;
	}

	public int lookup(String str) {
		return makeAStep(str, 0, buffer.duplicate());
	}

	private int makeAStep(String string, int pos, ByteBuffer node) {
		int sAddr = node.position(); // node start address
		int cAddr = sAddr + 5; // character lookup block address
		if (pos >= string.length()) {
			// returning trie value
			return node.getInt(sAddr + 1);
		}
		byte cnt = node.get(sAddr);
		int aAddr = cAddr + cnt; // refs block address
		byte expectedCharacter = safeCastCharacter(string.charAt(pos));
		for (int i = 0; i < cnt; i++) {
			byte character = node.get(cAddr + i);
			if (expectedCharacter == character) {
				int address = node.getInt(aAddr + i * 4);
				node.position(address);
				return makeAStep(string, pos + 1, node);
			}
		}
		return 0;
	}
}
