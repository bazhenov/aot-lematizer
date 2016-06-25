package me.bazhenov.aot.measure;

import me.bazhenov.aot.MapDictionary;
import me.bazhenov.aot.TernaryTreeDictionary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoadDictionaryBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.mode(Mode.SingleShotTime)
			.include(".*" + LoadDictionaryBenchmark.class.getSimpleName() + ".*")
			.forks(1)
			.timeUnit(TimeUnit.SECONDS)
			.build();
		new Runner(opt).run();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureTernaryTreeDictionaryLoad() throws IOException {
		TernaryTreeDictionary.loadDictionary();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureMapDictionaryLoad() throws IOException {
		MapDictionary.loadDictionary();
	}
}
