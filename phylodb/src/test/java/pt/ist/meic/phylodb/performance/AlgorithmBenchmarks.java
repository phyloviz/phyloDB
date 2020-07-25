package pt.ist.meic.phylodb.performance;

import org.openjdk.jmh.annotations.Benchmark;
import pt.ist.meic.phylodb.utils.DbUtils;

public class AlgorithmBenchmarks extends Benchmarks {

	public static void main(String[] args) throws Exception {
		main(AlgorithmBenchmarks.class, args);
	}

	@Benchmark
	public void goeBURST(WithProfiles ignored) {
		DbUtils.goeBURST(session, PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void radial(WithProfilesAndInference ignored) {
		DbUtils.radial(session, PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}
}
