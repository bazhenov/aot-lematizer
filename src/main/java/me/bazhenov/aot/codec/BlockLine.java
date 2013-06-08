package me.bazhenov.aot.codec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.base.Preconditions.*;
import static java.nio.charset.Charset.forName;

public final class BlockLine {

	public static final Charset CHARSET = forName("windows-1251");
	private final String affix;
	private final MorphRef[] morphRef;

	public BlockLine(String affix, MorphRef... morphRef) {
		this.affix = checkNotNull(affix);
		this.morphRef = checkNotNull(morphRef);
	}

	public void writeTo(ByteBuffer buffer) {
		int start = buffer.position();
		buffer.put((byte) 0);
		writeString(buffer, affix, CHARSET);

		buffer.put(checkCastToByte(morphRef.length));
		for (MorphRef ref : morphRef) {
			buffer.putInt(ref.lemmaId);
			writeShortArray(buffer, ref.ancodeIds);
		}
		int lineLength = buffer.position() - start;
		checkState(lineLength < 256);
		buffer.put(start, checkCastToByte(lineLength));
	}

	private static void writeShortArray(ByteBuffer buffer, short[] values) {
		buffer.put(checkCastToByte(values.length));
		for (short v : values) {
			buffer.putShort(v);
		}
	}

	private static short[] readShortArray(ByteBuffer buffer) {
		byte length = buffer.get();
		short[] result = new short[length];
		for (int i = 0; i < length; i++) {
			result[i] = buffer.getShort();
		}
		return result;
	}

	private static byte checkCastToByte(int value) {
		checkArgument(value < 256);
		return (byte) value;
	}

	public static void writeString(ByteBuffer b, String s, Charset c) {
		checkArgument(s.length() <= 255);
		byte[] bytes = s.getBytes(c);
		b.put(checkCastToByte(bytes.length));
		b.put(bytes);
	}

	public static String readString(ByteBuffer b, Charset c) {
		int length = b.get();
		byte[] bytes = new byte[length];
		b.get(bytes);
		return new String(bytes, c);
	}

	public static BlockLine readFrom(ByteBuffer buffer) {
		int lineEnd = buffer.position() + buffer.get();
		String affix = readString(buffer, CHARSET);
		return new BlockLine(affix, readMorphRefs(buffer));
	}

	public static MorphRef[] readMorphRefs(ByteBuffer buffer) {
		int morphCount = buffer.get();
		MorphRef[] refs = new MorphRef[morphCount];
		for (int i = 0; i < morphCount; i++) {
			int lemmaId = buffer.getInt();
			short[] ancodes = readShortArray(buffer);
			refs[i] = new MorphRef(lemmaId, ancodes);
		}
		return refs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockLine)) return false;

		BlockLine blockLine = (BlockLine) o;

		return affix.equals(blockLine.affix) && Arrays.equals(morphRef, blockLine.morphRef);
	}

	@Override
	public String toString() {
		return "BlockLine{" + affix + '/' + Arrays.toString(morphRef) + '}';
	}

	@Override
	public int hashCode() {
		int result = affix.hashCode();
		result = 31 * result + Arrays.hashCode(morphRef);
		return result;
	}

	public final static class MorphRef {

		private final int lemmaId;
		private final short[] ancodeIds;

		public MorphRef(int lemmaId, short[] ancodeIds) {
			this.lemmaId = lemmaId;
			this.ancodeIds = checkNotNull(ancodeIds);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MorphRef morphRef = (MorphRef) o;

			return lemmaId == morphRef.lemmaId && Arrays.equals(ancodeIds, morphRef.ancodeIds);
		}

		@Override
		public String toString() {
			return "MorphRef{" +
				"lemmaId=" + lemmaId +
				", ancodeIds=" + Arrays.toString(ancodeIds) +
				'}';
		}

		@Override
		public int hashCode() {
			int result = lemmaId;
			result = 31 * result + Arrays.hashCode(ancodeIds);
			return result;
		}
	}
}
