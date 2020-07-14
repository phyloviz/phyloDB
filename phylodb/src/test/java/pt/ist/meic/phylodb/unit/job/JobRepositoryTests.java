package pt.ist.meic.phylodb.unit.job;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.unit.RepositoryTestsContext;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static pt.ist.meic.phylodb.job.JobRepository.FULLY_QUALIFIED;
import static pt.ist.meic.phylodb.job.JobRepository.UUID_LENGTH;

public class JobRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Job[] STATE = new Job[]{JOB1, JOB2};

	private static Stream<Arguments> findAll_params() {
		Job first = new Job(PROJECT1.getPrimaryKey(), "1", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), "1", new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				second = new Job(PROJECT1.getPrimaryKey(), "2", Analysis.VISUALIZATION.getName() + "." + VisualizationAlgorithm.RADIAL.getName(), "2", new Object[] {DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()}),
				third = new Job(PROJECT1.getPrimaryKey(), "3", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), "3", new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				fourth = new Job(PROJECT1.getPrimaryKey(), "4", Analysis.VISUALIZATION.getName() + "." + VisualizationAlgorithm.RADIAL.getName(), "4", new Object[] {DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()});
		Job firstE = new Job(first.getPrimaryKey().getProjectId(), first.getPrimaryKey().getId(), true, false),
				state0 = new Job(STATE[0].getPrimaryKey().getProjectId(), STATE[0].getPrimaryKey().getId(), true, false),
				state1 = new Job(STATE[1].getPrimaryKey().getProjectId(), STATE[1].getPrimaryKey().getId(), true, false);
		return Stream.of(Arguments.of(0, new Job[0], Collections.emptyList()),
				Arguments.of(0, new Job[]{STATE[0]}, Collections.singletonList(state0)),
				Arguments.of(0, new Job[]{STATE[0], STATE[1], first}, Arrays.asList(firstE, state0)),
				Arguments.of(1, new Job[0], Collections.emptyList()),
				Arguments.of(1, new Job[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(1, new Job[]{STATE[0], STATE[1], first}, Collections.singletonList(state1)),
				Arguments.of(1, new Job[]{STATE[0], STATE[1], first, second}, Arrays.asList(state0, state1)),
				Arguments.of(2, new Job[0], Collections.emptyList()),
				Arguments.of(2, new Job[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(2, new Job[]{STATE[0], STATE[1], first, second, third}, Collections.singletonList(state1)),
				Arguments.of(2, new Job[]{STATE[0], STATE[1], first, second, third, fourth}, Arrays.asList(state0, state1)),
				Arguments.of(-1, new Job[0], Collections.emptyList()));
	}

	private static Stream<Arguments> exists_params() {
		Job first = new Job(PROJECT1.getPrimaryKey(), "1", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), "1", new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				second = new Job(PROJECT1.getPrimaryKey(), "2", Analysis.VISUALIZATION.getName() + "." + VisualizationAlgorithm.RADIAL.getName(), "2", new Object[] {DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()});
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Job[0], false),
				Arguments.of(first.getPrimaryKey(), new Job[]{first}, true),
				Arguments.of(first.getPrimaryKey(), new Job[]{second}, false),
				Arguments.of(null, new Job[0], false));
	}

	private static Stream<Arguments> save_params() {
		Job first = new Job(PROJECT1.getPrimaryKey(), "1", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), "1", new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				firstE = new Job(first.getPrimaryKey().getProjectId(), first.getPrimaryKey().getId(), true, false),
				state0 = new Job(STATE[0].getPrimaryKey().getProjectId(), STATE[0].getPrimaryKey().getId(), true, false),
				state1 = new Job(STATE[1].getPrimaryKey().getProjectId(), STATE[1].getPrimaryKey().getId(), true, false);
		return Stream.of(Arguments.of(first, new Job[0], new Job[]{firstE, state0, state1}, true),
				Arguments.of(null, new Job[0], new Job[]{state0, state1}, false));
	}

	private static Stream<Arguments> remove_params() {
		Job first = new Job(PROJECT1.getPrimaryKey(), "1", Analysis.INFERENCE.getName() + "." + InferenceAlgorithm.GOEBURST.getName(), "1", new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				state0 = new Job(STATE[0].getPrimaryKey().getProjectId(), STATE[0].getPrimaryKey().getId(), true, false),
				state1 = new Job(STATE[1].getPrimaryKey().getProjectId(), STATE[1].getPrimaryKey().getId(), true, false);
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Job[0], new Job[]{state0, state1}, false),
				Arguments.of(first.getPrimaryKey(), new Job[]{first}, new Job[]{state0, state1}, true),
				Arguments.of(null, new Job[0], new Job[]{state0, state1}, false));
	}

	private void store(Job[] jobs) {
		for (Job job : jobs) {
			Job.PrimaryKey key = job.getPrimaryKey();
			String jobName = name(key.getProjectId(), key.getId());
			String params = Arrays.stream(job.getParams())
					.map(s -> s.getClass().equals(String.class) ? "'" + s + "'" : String.valueOf(s))
					.collect(Collectors.joining(","));
			params = "'" + key.getProjectId() + "'," + params + ",'" + job.getAnalysisId() + "'";
			String statement = "CALL apoc.periodic.submit($, 'CALL ' + $ + '(' + $ + ')') YIELD name RETURN 0";
			execute(new Query(statement, jobName, String.format(FULLY_QUALIFIED, job.getAlgorithm()), params));
		}
	}

	private void clear() {
		execute(new Query("CALL apoc.periodic.list() yield name as x\n" +
				"CALL apoc.periodic.cancel(x) yield name as y return 0"));
	}

	private Job parse(Map<String, Object> row) {
		String name = (String) row.get("name");
		String project = name.substring(0, UUID_LENGTH);
		String id = name.substring(UUID_LENGTH + 1);
		return new Job(project,
				id,
				(boolean) row.get("completed"),
				(boolean) row.get("cancelled")
		);
	}

	private Job[] findAll() {
		String statement = "CALL apoc.periodic.list() YIELD name, done, cancelled\n" +
				"RETURN name as name, done as completed, cancelled as cancelled\n" +
				"ORDER BY size(name), name ";
		Result result = query(new Query(statement));
		if (result == null) return new Job[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Job[]::new);
	}

	private String name(String projectId, String jobId) {
		return projectId + "-" + jobId;
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(TAXON1);
		locusRepository.save(LOCUS1);
		locusRepository.save(LOCUS2);
		userRepository.save(USER1);
		projectRepository.save(PROJECT1);
		projectRepository.save(PROJECT2);
		schemaRepository.save(SCHEMA1);
		datasetRepository.save(DATASET1);
		alleleRepository.save(ALLELE11P);
		alleleRepository.save(ALLELE12);
		alleleRepository.save(ALLELE21);
		alleleRepository.save(ALLELE22);
		profileRepository.save(PROFILE1);
		profileRepository.save(PROFILE2);
		profileRepository.save(PROFILE3);
		inferenceRepository.save(INFERENCE1);
	}

	@AfterEach
	public void clean() {
		clear();
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Job[] state, List<Job> expected) {
		store(state);
		Optional<List<Job>> result = jobRepository.findAll(page, LIMIT, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId());
		if (expected.size() == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Job> jobs = result.get();
		assertEquals(expected.size(), jobs.size());
		assertArrayEquals(expected.toArray(), jobs.toArray());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Job.PrimaryKey key, Job[] state, boolean expected) {
		store(JobRepositoryTests.STATE);
		store(state);
		boolean result = jobRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Job inference, Job[] state, Job[] expectedState, boolean executed) {
		store(JobRepositoryTests.STATE);
		store(state);
		boolean result = jobRepository.save(inference);
		if (executed) {
			assertTrue(result);
		} else
			assertFalse(result);
		Job[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Job.PrimaryKey key, Job[] state, Job[] expectedState, boolean expectedResult) {
		store(JobRepositoryTests.STATE);
		store(state);
		boolean result = jobRepository.remove(key);
		Job[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

}
