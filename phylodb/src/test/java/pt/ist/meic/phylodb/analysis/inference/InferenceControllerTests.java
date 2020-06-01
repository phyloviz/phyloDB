package pt.ist.meic.phylodb.analysis.inference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.ControllerTestsContext;
import pt.ist.meic.phylodb.analysis.inference.model.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class InferenceControllerTests extends ControllerTestsContext {

	private static final String PROJECTID =  PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId();

	private static Stream<Arguments> getInferences_params() {
		String uri = "/projects/%s/datasets/%s/inferences";
		Entity<Inference.PrimaryKey> inference1 = new Entity<>(new Inference.PrimaryKey(PROJECTID, DATASETID, UUID.randomUUID().toString()), false),
				inference2 = new Entity<>(new Inference.PrimaryKey(PROJECTID, DATASETID, UUID.randomUUID().toString()), false);
		List<Entity<Inference.PrimaryKey>> inferences = new ArrayList<Entity<Inference.PrimaryKey>>() {{
			add(inference1);
			add(inference2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID)).param("page", "0"),
				req2 = get(String.format(uri, PROJECTID, DATASETID)), req3 = get(String.format(uri, PROJECTID, DATASETID)).param("page", "-10");
		List<InferenceOutputModel> result1 = inferences.stream()
				.map(InferenceOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, inferences, HttpStatus.OK, result1, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, inferences, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getInference_params() {
		String uri = "/projects/%s/datasets/%s/inferences/%s";
		List<Edge> edges = new ArrayList<Edge>() {{
			add(EDGES1);
			add(EDGES2);
		}};
		Inference.PrimaryKey key1 = INFERENCE1.getPrimaryKey();
		Inference.PrimaryKey key2 = INFERENCE2.getPrimaryKey();
		Inference inference1 = new Inference(PROJECTID, DATASETID, key1.getId(), InferenceAlgorithm.GOEBURST, edges);
		Inference inference2 = new Inference(PROJECTID, DATASETID, key2.getId(), InferenceAlgorithm.GOEBURST, edges);
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID, key1.getId())).param("format", TreeFormatter.NEXUS),
				req2 = get(String.format(uri, PROJECTID, DATASETID, key2.getId()));
		return Stream.of(Arguments.of(req1, inference1, HttpStatus.OK, new GetInferenceOutputModel(inference1, TreeFormatter.NEXUS)),
				Arguments.of(req2, inference2, HttpStatus.OK, new GetInferenceOutputModel(inference2, TreeFormatter.NEWICK)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> postInference_params() {
		String uri = "/projects/%s/datasets/%s/inferences";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "bytes".getBytes());
		MockHttpServletRequestBuilder req1 = multipart(String.format(uri, PROJECTID, DATASETID)).file(file).param("format", TreeFormatter.NEWICK).param("algorithm", InferenceAlgorithm.GOEBURST.getName()),
				req3 = multipart(String.format(uri, PROJECTID, DATASETID)).param("algorithm", InferenceAlgorithm.GOEBURST.getName()),
				req4 = post(String.format(uri, PROJECTID, DATASETID)).param("algorithm", InferenceAlgorithm.GOEBURST.getName()),
				req5 = multipart(String.format(uri, PROJECTID, DATASETID)).file(file),
				req6 = multipart(String.format(uri, PROJECTID, DATASETID)).file(file).param("format", TreeFormatter.NEWICK);
		String uuid = UUID.randomUUID().toString();
		return Stream.of(Arguments.of(req1, uuid, HttpStatus.CREATED, new CreatedOutputModel(uuid)),
				Arguments.of(req1, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req5, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req6, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteInference_params() {
		String uri = "/projects/%s/datasets/%s/inferences/%s";
		Inference.PrimaryKey key = INFERENCE1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, PROJECTID, DATASETID, key.getId()));
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
	@MethodSource("getInferences_params")
	public void getInferences(MockHttpServletRequestBuilder req, List<Entity<Inference.PrimaryKey>> inferences, HttpStatus expectedStatus, List<InferenceOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(inferenceService.getInferences(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(inferences));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
				}
			}
		} else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getInference_params")
	public void getInference(MockHttpServletRequestBuilder req, Inference inference, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(inferenceService.getInference(any(), any(), any())).thenReturn(Optional.ofNullable(inference));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			GetInferenceOutputModel actual = http.parseResult(GetInferenceOutputModel.class, result);
			assertEquals(expectedResult, actual);
		} else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postInference_params")
	public void postInference(MockHttpServletRequestBuilder req, String id, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(inferenceService.saveInference(any(), any(), anyString(), anyString(), any())).thenReturn(Optional.ofNullable(id));
		MockHttpServletResponse result = http.executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(CreatedOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteInference_params")
	public void deleteInference(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(inferenceService.deleteInference(any(), any(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
