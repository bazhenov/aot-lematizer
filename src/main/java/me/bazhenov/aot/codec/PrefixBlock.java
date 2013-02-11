package me.bazhenov.aot.codec;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static me.bazhenov.aot.codec.BlockLine.CHARSET;
import static me.bazhenov.aot.codec.ByteBufferUtils.checkedCast;

public class PrefixBlock {

	public static void writeTo(ByteBuffer buffer, Collection<Prefix> prefixes) {
		int startPosition = buffer.position();
		buffer.putInt(0);
		for (Prefix p : prefixes) {
			byte[] prefix = p.getPrefix().getBytes(CHARSET);
			buffer.put(checkedCast(prefix.length));
			buffer.put(prefix);
			buffer.putInt(p.getOffset());
		}
		buffer.putInt(startPosition, buffer.position() - startPosition);
	}

	public static Iterable<Prefix> readFrom(ByteBuffer buffer) {
		int startPosition = buffer.position();
		int EOB = buffer.getInt() + startPosition;
		List<Prefix> result = new LinkedList<Prefix>();
		while (buffer.position() < EOB) {
			byte prefixLength = buffer.get();
			byte[] prefix = new byte[prefixLength];
			buffer.get(prefix);
			int offset = buffer.getInt();
			result.add(new Prefix(new String(prefix, CHARSET), offset));
		}
		return result;
	}
}
