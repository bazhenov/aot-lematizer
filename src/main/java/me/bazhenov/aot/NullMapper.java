package me.bazhenov.aot;

public class NullMapper implements Mapper<String, Void> {

	public Void map(String input) {
		return null;
	}
}
