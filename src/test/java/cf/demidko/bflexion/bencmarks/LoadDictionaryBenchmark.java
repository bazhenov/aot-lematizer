package cf.demidko.bflexion.bencmarks;

import cf.demidko.bflexion.FlexionStorage;
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
	public void allDataLoading() throws IOException {
		new FlexionStorage();
	}
}
