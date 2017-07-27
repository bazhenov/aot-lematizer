package me.bazhenov.aot.measure;

import me.bazhenov.aot.MapDictionary;
import me.bazhenov.aot.MmapDictionary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

public class LookupBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.mode(Throughput)
			.include(".*" + LookupBenchmark.class.getSimpleName() + ".*")
			.forks(1)
			.timeUnit(MILLISECONDS)
			.build();
		new Runner(opt).run();
	}

	/*@Benchmark
	public void mMapDictionary(WordList list, MMapDictionaryHolder holder) throws IOException {
		holder.dictionary.getWordNorms(list.nextWord);
	}*/

	@Benchmark
	public void mapDictionary(WordList list, MapDictHolder holder) throws IOException {
		holder.dictionary.lookupWord(list.nextWord);
	}

	@State(Benchmark)
	public static class MapDictHolder {

		public MapDictionary dictionary;

		public MapDictHolder() {
			this.dictionary = MapDictionary.loadDictionary();
		}
	}

	@State(Benchmark)
	public static class MMapDictionaryHolder {

		public MmapDictionary dictionary;

		public MMapDictionaryHolder() {
			try {
				dictionary = new MmapDictionary(new File("/Users/bazhenov/Desktop/dictionary.dict"));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

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
