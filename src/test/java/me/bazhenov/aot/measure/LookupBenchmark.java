package me.bazhenov.aot.measure;

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

import static me.bazhenov.aot.TernaryTreeDictionary.loadDictionary;

/**
 * Created by wannabe on 23.06.16.
 */
public class LookupBenchmark {

	public static TernaryTreeDictionary TERNARY_TREE_DICTIONARY;

	static {
		try {
			TERNARY_TREE_DICTIONARY = loadDictionary();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.mode(Mode.AverageTime)
			.include(".*" + LookupBenchmark.class.getSimpleName() + ".*")
			.forks(1)
			.timeUnit(TimeUnit.MICROSECONDS)
			.build();
		new Runner(opt).run();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupShortWordTreeDictionary() throws IOException {
		TERNARY_TREE_DICTIONARY.lookupWord("мир");
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupLongWordTreeDictionary() throws IOException {
		TERNARY_TREE_DICTIONARY.lookupWord("клавиатура");
	}
}
