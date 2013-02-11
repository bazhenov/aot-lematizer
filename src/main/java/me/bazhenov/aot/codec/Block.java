package me.bazhenov.aot.codec;

import com.google.common.primitives.Ints;

import java.nio.ByteBuffer;

import static com.google.common.base.Preconditions.checkState;
import static me.bazhenov.aot.codec.BlockLine.CHARSET;

public class Block {

	public static void writeTo(ByteBuffer buffer, BlockLine[] lines) {
		checkState(lines.length < 256);
		buffer.put((byte) lines.length);
		for (BlockLine line : lines) {
			line.writeTo(buffer);
		}
	}

	/**
	 * Находит в текущем блоке заданный аффикс и возвращает морфологическую информацию о нём.
	 *
	 * @param buffer буффер указывающий на начало блока
	 * @param affix  аффикс
	 * @return морфологическая информация о слове
	 */
	public static BlockLine.MorphRef[] lookupAffix(ByteBuffer buffer, String affix) {
		byte[] affixBytes = affix.getBytes(CHARSET);

		byte blockSize = buffer.get();
		while (blockSize-- > 0) {
			int lineStart = buffer.position();
			byte lineLength = buffer.get();
			byte affixLength = buffer.get();
			byte[] pageAffix = new byte[affixLength];
			buffer.get(pageAffix);
			int cmp = compare(affixBytes, pageAffix);
			if (cmp > 0) {
				return null;
			} else if (cmp == 0) {
				return readLemmaRef(buffer);
			} else {
				buffer.position(lineStart + lineLength);
			}
		}
		return null;
	}

	private static BlockLine.MorphRef[] readLemmaRef(ByteBuffer buffer) {
		byte lemmaCount = buffer.get();
		BlockLine.MorphRef[] refs = new BlockLine.MorphRef[lemmaCount];
		for (int i = 0; i < lemmaCount; i++) {
			byte[] lemmaId = new byte[4];
			buffer.get(lemmaId, 1, 3);
			byte ancodeSz = buffer.get();
			short[] ancodes = new short[ancodeSz];
			for (int j = 0; j < ancodeSz; j++) {
				ancodes[j] = buffer.getShort();
			}
			refs[i] = new BlockLine.MorphRef(Ints.fromByteArray(lemmaId), ancodes);
		}
		return refs;
	}

	/**
	 * Сравнивает два аффикса.
	 *
	 * @param affixBytes первый аффикс
	 * @param pageAffix  второй аффикс
	 * @return {@code 1} если второй аффикс больше чем первый, {@code 0} если они равны, {@code -1} если первый аффикс
	 *         больше чем первый.
	 */
	public static int compare(byte[] affixBytes, byte[] pageAffix) {
		int l = Math.min(affixBytes.length, pageAffix.length);
		for (int i = 0; i < l; i++) {
			if (pageAffix[i] < affixBytes[i]) {
				return -1;
			} else if (pageAffix[i] > affixBytes[i]) {
				return 1;
			}
		}
		if (affixBytes.length > pageAffix.length) {
			return -1;
		} else if (affixBytes.length < pageAffix.length) {
			return 1;
		} else {
			return 0;
		}
	}
}
