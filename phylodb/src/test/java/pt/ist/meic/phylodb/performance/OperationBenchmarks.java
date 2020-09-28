package pt.ist.meic.phylodb.performance;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.FileUtils.createFile;
import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class OperationBenchmarks extends Benchmarks {


	@Test
	public void launchBenchmark() throws Exception {
		main(OperationBenchmarks.class);
	}

	@Benchmark
	public void getProfilesEntities(WithProfiles state) {
		profileService.getProfilesEntities(PROJECT_ID, DATASET_ID, 0, state.profiles);
	}

	@Benchmark
	public void getProfiles(WithProfiles state) {
		profileService.getProfiles(PROJECT_ID, DATASET_ID, 0, state.profiles);
	}

	@Benchmark
	public void getProfile(WithProfiles ignored) {
		profileService.getProfile(PROJECT_ID, DATASET_ID, "1", CURRENT_VERSION_VALUE);
	}

	@Benchmark
	public void saveProfile(WithProfiles ignored) {
		profileService.saveProfile(new Profile(PROJECT_ID, DATASET_ID, "teste", null, new String[] {"1", "1", "1", "1", "1", "1", "1"}), false);
	}

	@Benchmark
	public void saveProfiles(WithProfiles state) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", "profiles_" + state.profiles + ".txt"));
	}

	@Benchmark
	public void getInference(WithProfilesAndInference ignored) {
		inferenceService.getInference(PROJECT_ID, DATASET_ID, INFERENCE_ID);
	}

	@Benchmark
	public void getVisualization(WithProfilesInferenceAndVisualization ignored) {
		visualizationService.getVisualization(PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}
}
