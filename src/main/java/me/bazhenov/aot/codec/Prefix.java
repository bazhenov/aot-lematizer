package me.bazhenov.aot.codec;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Prefix {

	private final String prefix;
	private final int offset;

	public Prefix(String prefix, int offset) {
		this.prefix = checkNotNull(prefix);
		this.offset = offset;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Prefix prefix1 = (Prefix) o;

		return offset == prefix1.offset && prefix.equals(prefix1.prefix);

	}

	@Override
	public int hashCode() {
		int result = prefix.hashCode();
		result = 31 * result + offset;
		return result;
	}
}
