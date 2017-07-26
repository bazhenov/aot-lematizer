package me.bazhenov.aot.measure;

import me.bazhenov.aot.MapDictionary;
import me.bazhenov.aot.MmapDictionary;
import me.bazhenov.aot.TernaryTreeDictionary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static me.bazhenov.aot.TernaryTreeDictionary.loadDictionary;

public class LookupBenchmark {

	public static TernaryTreeDictionary TERNARY_TREE_DICTIONARY;
	public static MapDictionary MAP_DICTIONARY;

	private static MmapDictionary MMAP_DICTIONARY;

	static {
		try {
			TERNARY_TREE_DICTIONARY = loadDictionary();
			long before = System.currentTimeMillis();
			MAP_DICTIONARY = MapDictionary.loadDictionary();
			long after = System.currentTimeMillis();

			MMAP_DICTIONARY = new MmapDictionary(new File("/Users/bazhenov/Desktop/dictionary.dict"));

			System.out.println("\n\nTime taken: " + (after - before));
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
	public void measureLookupShortMMapDictionary() throws IOException {
		MMAP_DICTIONARY.getWordNorms("мир");
	}

	/*@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupLongMMapDictionary() throws IOException {
		MMAP_DICTIONARY.getWordNorms("клавиатура");
	}*/

	/*@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupShortWordTreeDictionary() throws IOException {
		TERNARY_TREE_DICTIONARY.lookupWord("мир");
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupLongWordTreeDictionary() throws IOException {
		TERNARY_TREE_DICTIONARY.lookupWord("клавиатура");
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupShortMapDictionary() throws IOException {
		MAP_DICTIONARY.lookupWord("мир");
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void measureLookupLongMapDictionary() throws IOException {
		MAP_DICTIONARY.lookupWord("клавиатура");
	}*/
}
