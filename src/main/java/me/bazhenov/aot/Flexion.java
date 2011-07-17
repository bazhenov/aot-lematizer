package me.bazhenov.aot;

public class Flexion {

	private final String ending;
	private final String ancode;

	public Flexion(String ending, String ancode) {
		this.ending = ending;
		this.ancode = ancode;
	}

	public String getEnding() {
		return ending;
	}

	public String getAncode() {
		return ancode;
	}

	@Override
	public String toString() {
		return "Flexion{" +
			"ending='" + ending + '\'' +
			", ancode='" + ancode + '\'' +
			'}';
	}
}
