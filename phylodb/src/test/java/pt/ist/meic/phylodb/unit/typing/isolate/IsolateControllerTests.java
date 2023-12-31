package pt.ist.meic.phylodb.unit.typing.isolate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.*;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.utils.service.Pair;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class IsolateControllerTests extends ControllerTestsContext {

	private static final String TAXONID = TAXON1.getPrimaryKey(), LOCUSID = LOCUS1.getPrimaryKey().getId();
	private static final String PROJECTID =  PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId();

	private static Stream<Arguments> getIsolatesList_params() {
		String uri = "/projects/%s/datasets/%s/isolates";
		Profile profile = new Profile(PROJECTID, DATASETID, "1", 1, false, null, null);
		List<VersionedEntity<Isolate.PrimaryKey>> isolates = new ArrayList<VersionedEntity<Isolate.PrimaryKey>>() {{
			add(new VersionedEntity<>(new Isolate.PrimaryKey(PROJECTID, DATASETID, "1"), 1, false));
			add(new VersionedEntity<>(new Isolate.PrimaryKey(PROJECTID, DATASETID, "2"), 1, false));
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID)).param("page", "0"),
				req2 = get(String.format(uri, PROJECTID, DATASETID)), req3 = get(String.format(uri, PROJECTID, DATASETID)).param("page", "-10");
		List<IsolateOutputModel> result1 = isolates.stream()
				.map(IsolateOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, isolates, HttpStatus.OK, result1, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, isolates, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getIsolatesFile_params() {
		String uri = "/projects/%s/datasets/%s/isolates/files";
		Profile profile = new Profile(PROJECTID, DATASETID, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(PROJECTID, DATASETID, "1", "description", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null);
		Isolate isolate2 = new Isolate(PROJECTID, DATASETID, "2", "test", new Ancillary[]{ANCILLARY1}, profile.getPrimaryKey().getId());
		List<Isolate> isolates = new ArrayList<Isolate>() {{
			add(isolate1);
			add(isolate2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID)).param("page", "0");
		FileOutputModel result3 = new FileOutputModel(new IsolatesFormatter().format(Collections.emptyList()));
		FileOutputModel result4 = new FileOutputModel(new IsolatesFormatter().format(isolates));
		return Stream.of(Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, result3, null),
				Arguments.of(req1, isolates, HttpStatus.OK, result4, null));
	}

	private static Stream<Arguments> getIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Profile profile = new Profile(PROJECTID, DATASETID, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(PROJECTID, DATASETID, "1", "description", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null);
		Isolate isolate2 = new Isolate(PROJECTID, DATASETID, "2", "test", new Ancillary[]{ANCILLARY1}, profile.getPrimaryKey().getId());
		Isolate.PrimaryKey key1 = isolate1.getPrimaryKey();
		Isolate.PrimaryKey key2 = isolate2.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID, key1.getId())).param("version", "1"),
				req2 = get(String.format(uri, PROJECTID, DATASETID, key2.getId()));
		return Stream.of(Arguments.of(req1, isolate1, HttpStatus.OK, new GetIsolateOutputModel(isolate1)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, isolate2, HttpStatus.OK, new GetIsolateOutputModel(isolate2)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Profile profile = new Profile(PROJECTID, DATASETID, "1", "aka", new String[]{"1", "2", "3"});
		Isolate isolate1 = new Isolate(PROJECTID, DATASETID, "1", "description", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null);
		Isolate.PrimaryKey key = isolate1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, PROJECTID, DATASETID, key.getId()));
		IsolateInputModel input1 = new IsolateInputModel(key.getId(), "aka", new Ancillary[]{ANCILLARY1}, profile.getPrimaryKey().getId()),
				input2 = new IsolateInputModel("different", "aka", null, null);
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> putIsolates_params() {
		String uri = "/projects/%s/datasets/%s/isolates/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "b".getBytes());
		MockMultipartHttpServletRequestBuilder req1 = multipart(String.format(uri, PROJECTID, DATASETID)).file(file),
				req2 = multipart(String.format(uri, PROJECTID, DATASETID));
		req1.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		req2.with(r -> {
			r.setMethod(HttpMethod.PUT.name());
			return r;
		});
		MockHttpServletRequestBuilder req3 = put(String.format(uri, TAXONID, LOCUSID));
		Integer[] invalidLines = {1, 2, 3};
		String[] invalidIds = {"4, 5"};
		return Stream.of(Arguments.of(req1, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req1, new Pair<>(new Integer[0], new String[0]), HttpStatus.OK, new BatchOutputModel(new Integer[0], new String[0])),
				Arguments.of(req1, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postIsolates_params() {
		String uri = "/projects/%s/datasets/%s/isolates/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "bytes".getBytes());
		MockHttpServletRequestBuilder req1 = multipart(String.format(uri, PROJECTID, DATASETID)).file(file),
				req3 = multipart(String.format(uri, PROJECTID, DATASETID)),
				req4 = post(String.format(uri, TAXONID, LOCUSID));
		Integer[] invalidLines = {1, 2, 3};
		String[] invalidIds = {"4, 5"};
		return Stream.of(Arguments.of(req1, new Pair<>(invalidLines, invalidIds), HttpStatus.OK, new BatchOutputModel(invalidLines, invalidIds)),
				Arguments.of(req1, new Pair<>(new Integer[0], new String[0]), HttpStatus.OK, new BatchOutputModel(new Integer[0], new String[0])),
				Arguments.of(req1, null, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, null, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteIsolate_params() {
		String uri = "/projects/%s/datasets/%s/isolates/%s";
		Isolate isolate = new Isolate(PROJECTID, DATASETID, "1", "description", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null);
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
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
	@MethodSource("getIsolatesList_params")
	public void getIsolatesList(MockHttpServletRequestBuilder req, List<VersionedEntity<Isolate.PrimaryKey>> isolates, HttpStatus expectedStatus, List<IsolateOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(isolateService.getIsolatesEntities(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(isolates));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
				}
			}
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getIsolatesFile_params")
	public void getProfilesFile(MockHttpServletRequestBuilder req, List<Isolate> isolates, HttpStatus expectedStatus, FileOutputModel expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(isolateService.getIsolates(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(isolates));
		MockHttpServletResponse result = executeRequest(req, MediaType.TEXT_PLAIN);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult.toResponseEntity().getBody(), result.getContentAsString());
		else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}
	@ParameterizedTest
	@MethodSource("getIsolate_params")
	public void getIsolate(MockHttpServletRequestBuilder req, Isolate isolate, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(isolateService.getIsolate(any(), any(), anyString(), anyLong())).thenReturn(Optional.ofNullable(isolate));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetIsolateOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveIsolate_params")
	public void putIsolate(MockHttpServletRequestBuilder req, IsolateInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(isolateService.saveIsolate(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putIsolates_params")
	public void putIsolates(MockHttpServletRequestBuilder req, Pair<Integer[], String[]> invalids, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(isolateService.saveIsolatesOnConflictUpdate(any(), any(), anyInt(), any())).thenReturn(Optional.ofNullable(invalids));
		MockHttpServletResponse result = executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(BatchOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postIsolates_params")
	public void postIsolates(MockHttpServletRequestBuilder req, Pair<Integer[], String[]> invalids, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(isolateService.saveIsolatesOnConflictSkip(any(), any(), anyInt(), any())).thenReturn(Optional.ofNullable(invalids));
		MockHttpServletResponse result = executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(BatchOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteIsolate_params")
	public void deleteIsolates(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(isolateService.deleteIsolate(any(), any(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
