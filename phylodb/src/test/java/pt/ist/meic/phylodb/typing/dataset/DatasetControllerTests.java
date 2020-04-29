package pt.ist.meic.phylodb.typing.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.MockHttp;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class DatasetControllerTests extends Test {

	@Autowired
	private DatasetController controller;
	@MockBean
	private DatasetService service;
	@MockBean
	private AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private MockHttp http;


	private static Stream<Arguments> getDatasets_params() {
		String uri = "/projects/%s/datasets";
		UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
		Entity<Schema.PrimaryKey> schemaReference = new Entity<>(new Schema.PrimaryKey("t", "x"), 1, false);
		Dataset dataset = new Dataset(projectId, datasetId, 1, false, "name1", schemaReference);
		List<Dataset> datasets = new ArrayList<Dataset>() {{add(dataset);}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId)).param("page", "0"),
				req2 = get(String.format(uri, projectId)), req3 = get(String.format(uri, projectId)).param("page", "-10");
		List<DatasetOutputModel> result = datasets.stream()
				.map(DatasetOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, datasets, HttpStatus.OK, result,  null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, datasets, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getProject_params() {
		String uri = "/projects/%s/datasets/%s";
		UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
		Entity<Schema.PrimaryKey> schemaReference = new Entity<>(new Schema.PrimaryKey("t", "x"), 1, false);
		Dataset dataset = new Dataset(projectId, datasetId, 1, false, "name1", schemaReference);
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("version", "1"),
				req2 = get(String.format(uri, projectId, datasetId));
		return Stream.of(Arguments.of(req1, dataset, HttpStatus.OK, new GetDatasetOutputModel(dataset)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, dataset, HttpStatus.OK, new GetDatasetOutputModel(dataset)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> putProject_params() {
		String uri = "/projects/%s/datasets/%s";
		UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, projectId, datasetId));
		DatasetInputModel input1 = new DatasetInputModel(datasetId, "description", "t", "x"),
			input2 = new DatasetInputModel(UUID.randomUUID(), null, null, "x");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postProject_params() {
		String uri = "/projects/%s/datasets";
		UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
		MockHttpServletRequestBuilder req1 = post(String.format(uri, projectId));
		DatasetInputModel input1 = new DatasetInputModel(datasetId, "description", "t", "x"),
				input2 = new DatasetInputModel(UUID.randomUUID(), null, null, "x");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.CREATED, null),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteProject_params() {
		String uri = "/projects/%s/datasets/%s";
		UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, projectId, datasetId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())));
	}

	@BeforeEach
	public void init(){
		MockitoAnnotations.initMocks(this);
		Mockito.when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
		Mockito.when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@ParameterizedTest
	@MethodSource("getDatasets_params")
	public void getDatasets(MockHttpServletRequestBuilder req, List<Dataset> datasets, HttpStatus expectedStatus, List<DatasetOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(service.getDatasets(any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(datasets));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if(expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId().toString(), p.get("id"));
					assertEquals(expectedResult.get(i).getProject_id().toString(), p.get("project_id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
					assertEquals(expectedResult.get(i).isDeprecated(), p.get("deprecated"));
				}
			}
		} else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getProject_params")
	public void getProject(MockHttpServletRequestBuilder req, Dataset dataset, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.getDataset(any(), any(), anyLong())).thenReturn(Optional.ofNullable(dataset));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, http.parseResult(GetDatasetOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putProject_params")
	public void updateProject(MockHttpServletRequestBuilder req, DatasetInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveDataset(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postProject_params")
	public void postProject(MockHttpServletRequestBuilder req, DatasetInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveDataset(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful()) {
			CreatedOutputModel parsed = http.parseResult(CreatedOutputModel.class, result);
			assertNotNull(parsed.getId());
		}
		else if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteProject_params")
	public void deleteProject(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.deleteDataset(any(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
