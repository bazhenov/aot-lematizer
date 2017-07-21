package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.util.List;

import static java.nio.ByteBuffer.allocate;
import static java.util.Comparator.naturalOrder;

public class MmapIntList {

	private ByteBuffer buffer;

	public MmapIntList(ByteBuffer buffer) {
		if (buffer.position() != 0) {
			throw new IllegalArgumentException("Buffer should be at zero position");
		}
		this.buffer = buffer;
	}

	public static ByteBuffer asByteBuffer(List<Integer> ints) {
		if (ints.isEmpty()) {
			throw new IllegalArgumentException("No empty list allowed");
		}
		ints.sort(naturalOrder());
		if (ints.get(0) <= 0) {
			throw new IllegalArgumentException("Only positive numbers allowed");
		}

		ByteBuffer buffer = allocate(ints.size() * 4);
		int previous = 0;
		for (int value : ints) {
			writeVInt(buffer, previous, value);
			previous = value;
		}

		buffer.flip();
		return buffer;
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

	public IntIterator iterator() {
		return new IntIterator();
	}

	public class IntIterator {

		private int position = 0;
		private int previous = 0;

		public int next() {
			if (position >= buffer.limit()) {
				return 0;
			}
			int value = buffer.getInt(position);
			if ((value & 0x80000000) != 0) {
				// если ведущий бит установлен, то число записно в виде двухбайтового, а н четырехбайтового числа
				value = (value >>> 16) & 0x7FFF;
				position += 2;
			} else {
				position += 4;
			}
			// Восстанавливаем дельта-кодирование
			value += previous;
			previous = value;
			return value;
		}
	}
}
