package com.farpost.aot;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
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
import static org.openjdk.jmh.annotations.Level.Invocation;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

public class LookupBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.mode(Throughput)
			.include(".*" + LookupBenchmark.class.getSimpleName() + ".*")
			.addProfiler(GCProfiler.class)
			.warmupIterations(5)
			.measurementIterations(10)
			.forks(1)
			.timeUnit(MILLISECONDS)
			.build();
		new Runner(opt).run();

	}

	@Benchmark
	public void lookup(WordList list, Holder holder) {
		holder.storage.search(list.nextWord);
	}


	@State(Benchmark)
	public static class Holder {

		FlexionStorage storage;

		public Holder() {
			try {
				storage = new FlexionStorage();
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

		@Setup(Invocation)
		public void setUp() {
			this.nextWord = words.get(wordId.getAndIncrement() % words.size());
		}
	}
}
