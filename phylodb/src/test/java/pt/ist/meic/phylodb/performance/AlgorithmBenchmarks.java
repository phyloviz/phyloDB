package pt.ist.meic.phylodb.performance;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import pt.ist.meic.phylodb.utils.DbUtils;

@ExtendWith(SpringExtension.class)
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
