package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

import static me.bazhenov.aot.CharacterUtils.safeCastCharacter;
import static me.bazhenov.aot.Utils.checkPositive;
import static me.bazhenov.aot.Utils.checkBufferIsReset;

public class MmapFlexionList {

	private ByteBuffer buffer;

	public MmapFlexionList(ByteBuffer buffer) {
		this.buffer = checkBufferIsReset(buffer);
	}

	public static Consumer<ByteBuffer> writeToByteBuffer(List<Flexion> flexion) {
		return b -> {
			for (Flexion f : flexion) {
				String ending = f.getEnding();
				if (ending.length() >= 128)
					throw new IllegalArgumentException("Ending is too long: " + ending.length());
				b.put((byte) ending.length());
				for (byte i = 0; i < ending.length(); i++) {
					b.put(safeCastCharacter(ending.charAt(i)));
				}
			}
		};
	}

	public byte[] retrievedNormPostfix(int offset) {
		byte l = buffer.get(offset);
		checkPositive(l);
		byte[] result = new byte[l];
		for (int i = 0; i < l; i++)
			result[i] = buffer.get(offset + i + 1);
		return result;
	}

}
