package pt.ist.meic.phylodb.performance;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import pt.ist.meic.phylodb.utils.DbUtils;

public class AlgorithmBenchmarks extends Benchmarks {

	@Test
	public void launchBenchMark() throws Exception {
		main(AlgorithmBenchmarks.class);
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
