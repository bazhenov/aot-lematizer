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
		State s = init();
		for (int i = 0; i < str.length(); i++) {
			if (!s.step(safeCastCharacter(str.charAt(i)))) {
				return 0;
			}
		}
		return s.value();
	}

	public State init() {
		return new State();
	}

	public class State {

		private int sAddr = 0;

		public int value() {
			return buffer.getInt(sAddr + 1);
		}

		public boolean step(byte character) {
			int cAddr = sAddr + 5; // character lookup block address
			byte cnt = buffer.get(sAddr);
			int aAddr = cAddr + cnt; // refs block address
			for (int i = 0; i < cnt; i++) {
				byte c = buffer.get(cAddr + i);
				if (c == character) {
					sAddr = buffer.getInt(aAddr + i * 4);
					return true;
				}
			}
			return false;
		}
	}
}
