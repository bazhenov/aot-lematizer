package me.bazhenov.aot;

import static com.google.common.base.Preconditions.checkNotNull;

public class Flexion {

	private final String ending;
	private final String ancode;
	private final String prefix;

	public Flexion(String ancode, String ending, String prefix) {
		this.ending = checkNotNull(ending);
		this.ancode = checkNotNull(ancode);
		this.prefix = checkNotNull(prefix);
	}

	public Flexion(String ancode, String ending) {
		this(ancode, ending, "");
	}

	public String getEnding() {
		return ending;
	}

	public String getAncode() {
		return ancode;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		return "Flexion{" +
			"ending='" + ending + '\'' +
			", ancode='" + ancode + '\'' +
			'}';
	}
}
