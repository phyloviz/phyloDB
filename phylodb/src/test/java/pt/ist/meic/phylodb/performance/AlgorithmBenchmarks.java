package pt.ist.meic.phylodb.performance;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.boot.SpringApplication;
import pt.ist.meic.phylodb.PhylodbApplication;
import pt.ist.meic.phylodb.utils.DbUtils;

import java.util.concurrent.TimeUnit;

public class AlgorithmBenchmarks extends Benchmarks {

	public static void main(String[] args) throws Exception {
		initContext(SpringApplication.run(PhylodbApplication.class, args));
		Options opts = new OptionsBuilder()
				.include("\\." + AlgorithmBenchmarks.class.getSimpleName() + "\\.")
				.timeout(TimeValue.NONE)
				.warmupIterations(5)
				.measurementIterations(10)
				.forks(0)
				.shouldDoGC(true)
				.shouldFailOnError(true)
				.jvmArgs("-server")
				.mode(Mode.AverageTime)
				.timeUnit(TimeUnit.MILLISECONDS)
				.addProfiler(GCProfiler.class)
				.build();
		new Runner(opts).run();
	}

	@Benchmark
	public void goeBURST500Profiles(Benchmarks.With500Profiles ignored) {
		DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void goeBURST1000Profiles(Benchmarks.With1000Profiles ignored) {
		DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void goeBURST2000Profiles(Benchmarks.With2000Profiles ignored) {
		DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void goeBURST5000Profiles(Benchmarks.With5000Profiles ignored) {
		DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void radial500Profiles(Benchmarks.With500ProfilesAndInference ignored) {
		DbUtils.radial(session, PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}

	@Benchmark
	public void radial1000Profiles(Benchmarks.With1000ProfilesAndInference ignored) {
		DbUtils.radial(session, PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}

	@Benchmark
	public void radial2000Profiles(Benchmarks.With2000ProfilesAndInference ignored) {
		DbUtils.radial(session, PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}

	@Benchmark
	public void radial5000Profiles(Benchmarks.With5000ProfilesAndInference ignored) {
		DbUtils.radial(session, PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}
}
