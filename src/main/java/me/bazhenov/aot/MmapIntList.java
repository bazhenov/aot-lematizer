package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.List;

import static java.lang.ThreadLocal.withInitial;
import static java.util.Comparator.naturalOrder;
import static me.bazhenov.aot.Utils.checkNonNegative;

public class MmapIntList {

	private ByteBuffer buffer;

	public MmapIntList(ByteBuffer buffer) {
		if (buffer.position() != 0) {
			throw new IllegalArgumentException("Buffer should be at zero position");
		}
		this.buffer = buffer;
	}

	public static void writeToByteBuffer(List<Integer> ints, ByteBuffer buffer) {
		if (ints.isEmpty()) {
			throw new IllegalArgumentException("No empty list allowed");
		}
		ints.sort(naturalOrder());
		if (ints.get(0) <= 0) {
			throw new IllegalArgumentException("Only positive numbers allowed");
		}

		if (ints.size() > 32_768) { // 2^15
			throw new IllegalArgumentException("Too big list detected");
		}
		buffer.putShort((short) ints.size());
		int previous = 0;
		for (int value : ints) {
			writeVInt(buffer, previous, value);
			previous = value;
		}
	}

	private static void writeVInt(ByteBuffer buffer, int previous, int value) {
		checkNonNegative(value);
		int delta = value - previous;
		do {
			byte b = (byte) (delta & 0x7F);
			delta >>>= 7;
			b |= delta > 0 ? 0x80 : 0;
			buffer.put(b);
		} while (delta != 0);
	}

	private final ThreadLocal<IntIterator> iterator = withInitial(() -> new IntIterator());

	public IntIterator iterator(int offset) {
		IntIterator iterator = this.iterator.get();

		iterator.left = buffer.getShort(offset);
		if (iterator.left <= 0) {
			throw new IllegalArgumentException("Illegal list length at offset: " + offset);
		}
		iterator.offset = offset + 2;
		iterator.previousValue = 0;
		return iterator;
	}

	public class IntIterator {

		private int offset = 0;
		private int previousValue = 0;
		private int left;

		public boolean hasNext() {
			return left > 0;
		}

		public int nextCommon(MmapIntList.IntIterator other) {
			if (!other.hasNext() || !hasNext())
				return 0;
			int a = next();
			int b = other.next();
			while (a != b) {
				if (a < b) {
					while (a < b && hasNext()) {
						a = next();
					}
					if (a < b && !hasNext())
						return 0;
				} else {
					while (b < a && other.hasNext()) {
						b = other.next();
					}
					if (b < a && !other.hasNext())
						return 0;
				}
			}
			return a;
		}

		public int next() {
			if (!hasNext()) {
				return 0;
			}
			int value = readVInt();
			// Восстанавливаем дельта-кодирование
			value += previousValue;
			previousValue = value;
			left--;
			return value;
		}

		private int readVInt() {
			byte b = buffer.get(offset++);
			if (b >= 0) return b;
			int i = b & 0x7F;
			b = buffer.get(offset++);
			i |= (b & 0x7F) << 7;
			if (b >= 0) return i;
			b = buffer.get(offset++);
			i |= (b & 0x7F) << 14;
			if (b >= 0) return i;
			b = buffer.get(offset++);
			i |= (b & 0x7F) << 21;
			if (b >= 0) return i;
			b = buffer.get(offset++);
			i |= (b & 0x0F) << 28;
			if ((b & 0xF0) == 0) return i;
			throw new IllegalStateException("Invalid vInt detected (too many bits)");
		}
	}
}
