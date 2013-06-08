package me.bazhenov.aot.codec;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static me.bazhenov.aot.codec.BlockLine.*;

public final class PrefixBlock {

	private final List<Prefix> prefixes;

	private PrefixBlock(List<Prefix> prefixes) {
		this.prefixes = checkNotNull(prefixes);
	}

	public static PrefixBlock createFromSortedList(List<Prefix> prefixes) {
		return new PrefixBlock(prefixes);
	}

	public void writeTo(ByteBuffer buffer) {
		int startPosition = buffer.position();
		buffer.putInt(0);
		for (Prefix p : prefixes) {
			writeString(buffer, p.getPrefix(), CHARSET);
			buffer.putInt(p.getOffset());
		}
		buffer.putInt(startPosition, buffer.position() - startPosition);
	}

	public static PrefixBlock readFrom(ByteBuffer buffer) {
		int startPosition = buffer.position();
		int EOB = buffer.getInt() + startPosition;
		List<Prefix> result = newArrayList();
		while (buffer.position() < EOB) {
			String prefix = readString(buffer, CHARSET);
			int offset = buffer.getInt();
			result.add(new Prefix(prefix, offset));
		}
		return new PrefixBlock(result);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PrefixBlock)) return false;

		PrefixBlock that = (PrefixBlock) o;

		return prefixes.equals(that.prefixes);
	}

	@Override
	public int hashCode() {
		return prefixes.hashCode();
	}

	public Collection<Prefix> lookupPrefixes(String word) {
		word = word.toLowerCase();
		Collection<Prefix> result = newLinkedList();
		for (Prefix prefix : prefixes) {
			if (word.startsWith(prefix.getPrefix())) {
				result.add(prefix);
			}
		}
		return result;
	}
}
