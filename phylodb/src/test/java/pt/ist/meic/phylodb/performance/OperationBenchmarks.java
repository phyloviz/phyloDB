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
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static pt.ist.meic.phylodb.utils.FileUtils.createFile;
import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class OperationBenchmarks extends Benchmarks {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset";

	public static void main(String[] args) throws Exception {
		initContext(SpringApplication.run(PhylodbApplication.class, args));
		Options opts = new OptionsBuilder()
				.include("\\." + OperationBenchmarks.class.getSimpleName() + "\\.")
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
	public void getProfilesEntities500(Benchmarks.With500Profiles ignored) {
		profileService.getProfilesEntities(PROJECT_ID, DATASET_ID, 0, 500);
	}

	@Benchmark
	public void getProfilesEntities1000(Benchmarks.With1000Profiles ignored) {
		profileService.getProfilesEntities(PROJECT_ID, DATASET_ID, 0, 1000);
	}

	@Benchmark
	public void getProfilesEntities2000(Benchmarks.With2000Profiles ignored) {
		profileService.getProfilesEntities(PROJECT_ID, DATASET_ID, 0, 2000);
	}

	@Benchmark
	public void getProfilesEntities5000(Benchmarks.With5000Profiles ignored) {
		profileService.getProfilesEntities(PROJECT_ID, DATASET_ID, 0, 5000);
	}

	@Benchmark
	public void getProfiles500(Benchmarks.With500Profiles ignored) {
		profileService.getProfiles(PROJECT_ID, DATASET_ID, 0, 500);
	}

	@Benchmark
	public void getProfiles1000(Benchmarks.With1000Profiles ignored) {
		profileService.getProfiles(PROJECT_ID, DATASET_ID, 0, 1000);
	}

	@Benchmark
	public void getProfiles2000(Benchmarks.With2000Profiles ignored) {
		profileService.getProfiles(PROJECT_ID, DATASET_ID, 0, 2000);
	}

	@Benchmark
	public void getProfiles5000(Benchmarks.With5000Profiles ignored) {
		profileService.getProfiles(PROJECT_ID, DATASET_ID, 0, 5000);
	}

	@Benchmark
	public void getProfile500(Benchmarks.With500Profiles ignored) {
		profileService.getProfile(PROJECT_ID, DATASET_ID, "1", CURRENT_VERSION_VALUE);
	}

	@Benchmark
	public void getProfile1000(Benchmarks.With1000Profiles ignored) {
		profileService.getProfile(PROJECT_ID, DATASET_ID, "1", CURRENT_VERSION_VALUE);
	}

	@Benchmark
	public void getProfile2000(Benchmarks.With2000Profiles ignored) {
		profileService.getProfile(PROJECT_ID, DATASET_ID, "1", CURRENT_VERSION_VALUE);
	}

	@Benchmark
	public void getProfile5000(Benchmarks.With5000Profiles ignored) {
		profileService.getProfile(PROJECT_ID, DATASET_ID, "1", CURRENT_VERSION_VALUE);
	}

	@Benchmark
	public void saveProfile500(Benchmarks.With500Profiles ignored) {
		profileService.saveProfile(new Profile(PROJECT_ID, DATASET_ID, "teste", null, new String[] {"1", "1", "1", "1", "1", "1", "1"}), false);
	}

	@Benchmark
	public void saveProfile1000(Benchmarks.With1000Profiles ignored) {
		profileService.saveProfile(new Profile(PROJECT_ID, DATASET_ID, "teste", null, new String[] {"1", "1", "1", "1", "1", "1", "1"}), false);
	}

	@Benchmark
	public void saveProfile2000(Benchmarks.With2000Profiles ignored) {
		profileService.saveProfile(new Profile(PROJECT_ID, DATASET_ID, "teste", null, new String[] {"1", "1", "1", "1", "1", "1", "1"}), false);
	}

	@Benchmark
	public void saveProfile5000(Benchmarks.With5000Profiles ignored) {
		profileService.saveProfile(new Profile(PROJECT_ID, DATASET_ID, "teste", null, new String[] {"1", "1", "1", "1", "1", "1", "1"}), false);
	}

	@Benchmark
	public void saveProfiles500(Benchmarks.With500Profiles ignored) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", "profiles_500.txt"));
	}

	@Benchmark
	public void saveProfiles1000(Benchmarks.With1000Profiles ignored) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", "profiles_1000.txt"));
	}

	@Benchmark
	public void saveProfiles2000(Benchmarks.With2000Profiles ignored) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", "profiles_2000.txt"));
	}

	@Benchmark
	public void saveProfiles5000(Benchmarks.With5000Profiles ignored) throws IOException {
		profileService.saveProfilesOnConflictUpdate(PROJECT_ID, DATASET_ID, false, createFile("performance", "profiles_5000.txt"));
	}

}
