package me.bazhenov.aot;

public interface Mapper<I, O> {

	public O map(I input);
}
