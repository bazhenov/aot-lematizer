package me.bazhenov.aot;

public class GramInfo {

	private final String description;

	public GramInfo(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "GramInfo{" +
			"description='" + description + '\'' +
			'}';
	}
}
