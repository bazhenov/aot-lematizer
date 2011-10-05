package me.bazhenov.aot;

public class GramInfo {

	private final String description;

	public GramInfo(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "GramInfo{" +
			"description='" + description + '\'' +
			'}';
	}
}
