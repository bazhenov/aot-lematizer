package me.bazhenov.aot.codec;

import com.google.common.primitives.Ints;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.charset.Charset.forName;

public class BlockLine {

	public static final Charset CHARSET = forName("windows-1251");
	private final String affix;
	private final MorphRef[] morphRef;

	public BlockLine(String affix, MorphRef... morphRef) {
		this.affix = checkNotNull(affix);
		this.morphRef = checkNotNull(morphRef);
	}

	public void writeTo(ByteBuffer buffer) {
		byte[] bytes = this.affix.getBytes(CHARSET);
		int start = buffer.position();
		buffer.put((byte) 0);
		buffer.put((byte) bytes.length);
		buffer.put(bytes);
		buffer.put((byte) morphRef.length);
		for (MorphRef ref : morphRef) {
			bytes = Ints.toByteArray(ref.lemmaId);
			checkState(bytes[0] == 0);
			buffer.put(bytes, 1, 3);
			buffer.put((byte) ref.ancodeIds.length);
			for (short ancodeId : ref.ancodeIds) {
				buffer.putShort(ancodeId);
			}
		}
		int lineLength = buffer.position() - start;
		checkState(lineLength < 256);
		buffer.put(start, (byte) lineLength);
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
		public int hashCode() {
			int result = lemmaId;
			result = 31 * result + Arrays.hashCode(ancodeIds);
			return result;
		}
	}
}
