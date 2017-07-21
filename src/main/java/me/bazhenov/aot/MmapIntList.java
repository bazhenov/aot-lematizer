package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.List;

import static java.util.Comparator.naturalOrder;

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

		if (ints.size() > 128) {
			throw new IllegalArgumentException("Too big list detected");
		}
		buffer.put((byte) ints.size());
		int previous = 0;
		for (int value : ints) {
			writeVInt(buffer, previous, value);
			previous = value;
		}
	}

	private static void writeVInt(ByteBuffer buffer, int previous, int value) {
		int delta = value - previous;
		if (delta <= 0x7FFF) {
			// число вписывается в 15 бит, пишем в виде двух байт с ведущим установленными битом
			buffer.putShort((short) (delta & 0x7FFFF | 0x8000));
		} else {
			// число не вписывается в 15 бит, пишем в виде четырех байт
			buffer.putInt(delta);
		}
	}

	public IntIterator iterator(int offset) {
		return new IntIterator(offset);
	}

	public class IntIterator {

		private int offset = 0;
		private int previousValue = 0;
		private int left;

		public IntIterator(int offset) {
			this.left = buffer.get(offset);
			this.offset = offset + 1;
		}

		public boolean hasNext() {
			return left > 0;
		}

		public int next() {
			if (!hasNext()) {
				return 0;
			}
			int value = buffer.getShort(offset);
			if ((value & 0x8000) != 0) {
				// если ведущий бит установлен, то число записно в виде двухбайтового, а не четырехбайтового числа
				value = value & 0x7FFF;
				offset += 2;
			} else {
				value = buffer.getInt(offset);
				offset += 4;
			}
			// Восстанавливаем дельта-кодирование
			value += previousValue;
			previousValue = value;
			left--;
			return value;
		}
	}
}
