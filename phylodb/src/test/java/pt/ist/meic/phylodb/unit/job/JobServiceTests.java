package pt.ist.meic.phylodb.unit.job;

import pt.ist.meic.phylodb.utils.service.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.unit.ServiceTestsContext;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

public class JobServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Job[] STATE = new Job[]{JOB1, JOB2};

	private static Stream<Arguments> getJobs_params() {
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, Collections.singletonList(JOB1)),
				Arguments.of(0, Arrays.asList(STATE)),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> createJob_params() {
		JobRequest request1 = new JobRequest(Analysis.INFERENCE, InferenceAlgorithm.GOEBURST.getName(), new Object[] {DATASET1.getPrimaryKey().getId(), 3}),
				request2 = new JobRequest(Analysis.VISUALIZATION, VisualizationAlgorithm.RADIAL.getName(), new Object[] {DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()});
		List<Entity<Profile.PrimaryKey>> profiles1 = Collections.singletonList(new Entity<>(PROFILE1.getPrimaryKey(), false)),
				profiles2 = Arrays.asList(new Entity<>(PROFILE1.getPrimaryKey(), false), new Entity<>(PROFILE2.getPrimaryKey(), false));
		return Stream.of(Arguments.of(request1, Collections.emptyList(), true, true, false),
				Arguments.of(request1, null, true, true, false),
				Arguments.of(request1, profiles1, true, true, false),
				Arguments.of(request1, profiles2, false, false, false),
				Arguments.of(request1, profiles2, false, true, true),
				Arguments.of(request2, Collections.emptyList(), false, true, false),
				Arguments.of(request2, Collections.emptyList(), true, false, false),
				Arguments.of(request2, profiles1, true, true, true));
	}

	private static Stream<Arguments> deleteJob_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getJobs_params")
	public void getInferences(int page, List<Job> expected) {
		Mockito.when(jobRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Job>> result = jobService.getJobs(PROJECT1.getPrimaryKey(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Job> inferences = result.get();
		assertEquals(expected.size(), inferences.size());
		assertEquals(expected, inferences);
	}

	@ParameterizedTest
	@MethodSource("createJob_params")
	public void saveInference(JobRequest jobRequest, List<VersionedEntity<Profile.PrimaryKey>> profiles, boolean inference, boolean result, boolean expected) throws IOException {
		Mockito.when(profileRepository.findAllEntities(anyInt(), anyInt(), any(), any())).thenReturn(Optional.ofNullable(profiles));
		Mockito.when(inferenceRepository.exists(any())).thenReturn(inference);
		Mockito.when(jobRepository.save(any())).thenReturn(result);
		String projectId = UUID.randomUUID().toString();
		Optional<Pair<String, String>> actual = jobService.createJob(projectId, jobRequest);
		assertEquals(expected, actual.isPresent());
	}

	@ParameterizedTest
	@MethodSource("deleteJob_params")
	public void deleteInference(Job.PrimaryKey key, boolean expected) {
		Mockito.when(jobRepository.remove(any())).thenReturn(expected);
		boolean result = jobService.deleteJob(key.getProjectId(), key.getId());
		assertEquals(expected, result);
	}

}
