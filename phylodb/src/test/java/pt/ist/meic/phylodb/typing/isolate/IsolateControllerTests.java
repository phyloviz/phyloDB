package pt.ist.meic.phylodb.typing.isolate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.typing.isolate.model.*;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.MockHttp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IsolateControllerTests extends Test {

	private static final String taxonId = "t", locusId = "t";
	private static final UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();
	private static final Ancillary ancillary1 = new Ancillary("key1", "value1");
	private static final Ancillary ancillary2 = new Ancillary("key2", "value2");

	@InjectMocks
	private IsolateController controller;
	@MockBean
	private IsolateService service;
	@MockBean
	private AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private MockHttp http;

	private static Stream<Arguments> getIsolatesList_params() {
		String uri = "/projects/%s/datasets/%s/isolates";
		Profile profile = new Profile(projectId, datasetId, "1", 1, false, null, null);
		Isolate isolate1 = new Isolate(projectId, datasetId, "1", "description", new Ancillary[]{ancillary1, ancillary2}, null);
		Isolate isolate2 = new Isolate(projectId, datasetId, "2", "test", new Ancillary[]{ancillary1}, profile.getPrimaryKey().getId());
		List<Isolate> isolates = new ArrayList<Isolate>() {{
			add(isolate1);
			add(isolate2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("page", "0"),
				req2 = get(String.format(uri, projectId, datasetId)), req3 = get(String.format(uri, projectId, datasetId)).param("page", "-10");
		List<IsolateOutputModel> result1 = isolates.stream()
				.map(IsolateOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, isolates, HttpStatus.OK, result1, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, isolates, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getIsolatesString_params() {
		String uri = "/projects/%s/datasets/%s/isolates";
		Profile profile = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(projectId, datasetId, "1", "description", new Ancillary[]{ancillary1, ancillary2}, null);
		Isolate isolate2 = new Isolate(projectId, datasetId, "2", "test", new Ancillary[]{ancillary1}, profile.getPrimaryKey().getId());
		List<Isolate> isolates = new ArrayList<Isolate>() {{
			add(isolate1);
			add(isolate2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("page", "0");
		FileOutputModel result3 = new FileOutputModel(new IsolatesFormatter().format(Collections.emptyList()));
		FileOutputModel result4 = new FileOutputModel(new IsolatesFormatter().format(isolates));
		return Stream.of(Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, result3, null),
				Arguments.of(req1, isolates, HttpStatus.OK, result4, null));
	}

	private static Stream<Arguments> getIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Profile profile = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(projectId, datasetId, "1", "description", new Ancillary[]{ancillary1, ancillary2}, null);
		Isolate isolate2 = new Isolate(projectId, datasetId, "2", "test", new Ancillary[]{ancillary1}, profile.getPrimaryKey().getId());
		Isolate.PrimaryKey key1 = isolate1.getPrimaryKey();
		Isolate.PrimaryKey key2 = isolate2.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId, key1.getId())).param("version", "1"),
				req2 = get(String.format(uri, projectId, datasetId, key2.getId()));
		return Stream.of(Arguments.of(req1, isolate1, HttpStatus.OK, new GetIsolateOutputModel(isolate1)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, isolate2, HttpStatus.OK, new GetIsolateOutputModel(isolate2)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Profile profile = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(projectId, datasetId, "1", "description", new Ancillary[]{ancillary1, ancillary2}, null);
		Isolate.PrimaryKey key = isolate1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, projectId, datasetId, key.getId()));
		IsolateInputModel input1 = new IsolateInputModel(key.getId(), "aka", new Ancillary[]{ancillary1}, profile.getPrimaryKey().getId()),
				input2 = new IsolateInputModel("different", "aka", null, null);
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> putIsolates_params() {
		String uri = "/projects/%s/datasets/%s/isolates/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "b".getBytes());
		MockMultipartHttpServletRequestBuilder req1 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req2 = multipart(String.format(uri, projectId, datasetId));
		req1.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		req2.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		MockHttpServletRequestBuilder req3 = put(String.format(uri, taxonId, locusId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req3, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postIsolates_params() {
		String uri = "/projects/%s/datasets/%s/isolates/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "bytes".getBytes());
		MockHttpServletRequestBuilder req1 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req3 = multipart(String.format(uri, projectId, datasetId)),
				req4 = post(String.format(uri, taxonId, locusId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Isolate isolate = new Isolate(projectId, datasetId, "1", "description", new Ancillary[]{ancillary1, ancillary2}, null);
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, projectId, datasetId, key.getId()));
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
	@MethodSource("getIsolatesList_params")
	public void getIsolatesList(MockHttpServletRequestBuilder req, List<Isolate> isolates, HttpStatus expectedStatus, List<IsolateOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(service.getIsolates(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(isolates));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getProject_id().toString(), p.get("project_id"));
					assertEquals(expectedResult.get(i).getDataset_id().toString(), p.get("dataset_id"));
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
					assertEquals(expectedResult.get(i).isDeprecated(), p.get("deprecated"));
				}
			}
		} else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getIsolatesString_params")
	public void getProfilesString(MockHttpServletRequestBuilder req, List<Isolate> isolates, HttpStatus expectedStatus, FileOutputModel expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(service.getIsolates(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(isolates));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.TEXT_PLAIN);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult.toResponseEntity().getBody(), result.getContentAsString());
		else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getIsolate_params")
	public void getIsolate(MockHttpServletRequestBuilder req, Isolate isolate, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.getIsolate(any(), any(), anyString(), anyLong())).thenReturn(Optional.ofNullable(isolate));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, http.parseResult(GetIsolateOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveIsolate_params")
	public void putIsolate(MockHttpServletRequestBuilder req, IsolateInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(service.saveIsolate(any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putIsolates_params")
	public void putIsolates(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveIsolatesOnConflictUpdate(any(), any(), anyInt(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postIsolates_params")
	public void postIsolates(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveIsolatesOnConflictSkip(any(), any(), anyInt(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteIsolate_params")
	public void deleteIsolates(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.deleteIsolate(any(), any(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
