package pt.ist.meic.phylodb.unit.job;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobAcceptedOutputModel;
import pt.ist.meic.phylodb.job.model.JobInputModel;
import pt.ist.meic.phylodb.job.model.JobOutputModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class JobControllerTests extends ControllerTestsContext {

	private static final String PROJECTID =  PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId();

	private static Stream<Arguments> getJobs_params() {
		String uri = "/projects/%s/jobs";
		List<Job> jobs = new ArrayList<Job>() {{
			add(JOB1);
			add(JOB2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID)).param("page", "0"),
				req2 = get(String.format(uri, PROJECTID)), req3 = get(String.format(uri, PROJECTID)).param("page", "-10");
		List<JobOutputModel> result1 = jobs.stream()
				.map(JobOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, jobs, HttpStatus.OK, result1, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, jobs, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postJob_params() {
		String uri = "/projects/%s/jobs";
		MockHttpServletRequestBuilder req = post(String.format(uri, PROJECTID));
		String inference = Analysis.INFERENCE.getName(), goeburst = InferenceAlgorithm.GOEBURST.getName(),
				visualization = Analysis.VISUALIZATION.getName(), radial = VisualizationAlgorithm.RADIAL.getName();
		Pair<String, String> result = new Pair<>(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		JobInputModel input1 = new JobInputModel("something", goeburst, new Object[] {DATASETID, 3}),
				input2 = new JobInputModel(inference, "something", new Object[] {DATASETID, 3}),
				input3 = new JobInputModel(inference, goeburst, new Object[1]),
				input4 = new JobInputModel(inference, goeburst, new Object[2]),
				input5 = new JobInputModel(inference, goeburst, new Object[] {DATASETID, 3}),
				input7 = new JobInputModel(visualization, "something", new Object[] {DATASETID, 3}),
				input8 = new JobInputModel(visualization, radial, new Object[1]),
				input9 = new JobInputModel(visualization, radial, new Object[2]),
				input10 = new JobInputModel(inference, goeburst, new Object[] {DATASETID, INFERENCE1.getPrimaryKey().getId()});
		return Stream.of(Arguments.of(req, input5, result, HttpStatus.ACCEPTED, new JobAcceptedOutputModel(result.getKey(), result.getValue())),
				Arguments.of(req, input10, result, HttpStatus.ACCEPTED, new JobAcceptedOutputModel(result.getKey(), result.getValue())),
				Arguments.of(req, input1, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input2, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input4, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input7, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input8, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input9, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req, input10, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())));
	}

	private static Stream<Arguments> deleteJob_params() {
		String uri = "/projects/%s/jobs/%s";
		Job.PrimaryKey key = JOB1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, PROJECTID, key.getId()));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
		Mockito.when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@ParameterizedTest
	@MethodSource("getJobs_params")
	public void getInferences(MockHttpServletRequestBuilder req, List<Job> jobs, HttpStatus expectedStatus, List<JobOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(jobService.getJobs(any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(jobs));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).isCancelled(), p.get("cancelled"));
					assertEquals(expectedResult.get(i).isCompleted(), p.get("completed"));
				}
			}
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postJob_params")
	public void postInference(MockHttpServletRequestBuilder req, JobInputModel input, Pair<String, String> result, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(jobService.createJob(any(), any())).thenReturn(Optional.ofNullable(result));
		MockHttpServletResponse actual = executeRequest(req, input);
		assertEquals(expectedStatus.value(), actual.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, actual));
		else
			assertEquals(expectedResult, parseResult(JobAcceptedOutputModel.class, actual));
	}

	@ParameterizedTest
	@MethodSource("deleteJob_params")
	public void deleteInference(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(jobService.deleteJob(anyString(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
