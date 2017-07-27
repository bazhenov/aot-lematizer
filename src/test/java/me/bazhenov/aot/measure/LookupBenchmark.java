package me.bazhenov.aot.measure;

import me.bazhenov.aot.MapDictionary;
import me.bazhenov.aot.MmapDictionary;
import me.bazhenov.aot.TernaryTreeDictionary;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import static java.lang.System.currentTimeMillis;
import static me.bazhenov.aot.TernaryTreeDictionary.loadDictionary;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

public class LookupBenchmark {

	public static TernaryTreeDictionary TERNARY_TREE_DICTIONARY;
	public static MapDictionary MAP_DICTIONARY;

	private static MmapDictionary MMAP_DICTIONARY;

	static {
		try {
			TERNARY_TREE_DICTIONARY = loadDictionary();

			long before = currentTimeMillis();
			MAP_DICTIONARY = MapDictionary.loadDictionary();
			long after = currentTimeMillis();
			System.out.println("\n\nTime taken: " + (after - before));

			MMAP_DICTIONARY = new MmapDictionary(new File("/Users/bazhenov/Desktop/dictionary.dict"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.mode(Throughput)
			.include(".*" + LookupBenchmark.class.getSimpleName() + ".*")
			.forks(1)
			.timeUnit(TimeUnit.MICROSECONDS)
			.build();
		new Runner(opt).run();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void mMapDictionary(WordList list) throws IOException {
		MMAP_DICTIONARY.getWordNorms(list.nextWord);
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void mapDictionary(WordList list) throws IOException {
		MAP_DICTIONARY.lookupWord(list.nextWord);
	}

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

	@State(Benchmark)
	public static class WordList {

		final List<String> words = new ArrayList<>();
		final AtomicInteger wordId = new AtomicInteger(0);
		String nextWord;

		public WordList() {
			try (InputStream is = getClass().getResourceAsStream("/random-words.txt.gz");
					 InputStream gzip = new GZIPInputStream(is);
					 BufferedReader reader = new BufferedReader(new InputStreamReader(gzip))) {
				reader.lines().sequential().forEach(words::add);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Setup(Level.Invocation)
		public void setUp() {
			this.nextWord = words.get(wordId.getAndIncrement() % words.size());
		}
	}
}
