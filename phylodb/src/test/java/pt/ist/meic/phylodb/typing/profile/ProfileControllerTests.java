package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
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
import pt.ist.meic.phylodb.io.formatters.dataset.profile.MlFormatter;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.SnpFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.security.authentication.AuthenticationInterceptor;
import pt.ist.meic.phylodb.security.authorization.AuthorizationInterceptor;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.GetProfileOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.profile.model.ProfileInputModel;
import pt.ist.meic.phylodb.typing.profile.model.ProfileOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.MockHttp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ProfileControllerTests extends Test {

	@InjectMocks
	private ProfileController controller;
	@MockBean
	private ProfileService service;
	@MockBean
	private AuthenticationInterceptor authenticationInterceptor;
	@MockBean
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private MockHttp http;

	private static final String taxonId = "t", locusId = "t";
	private static final UUID projectId = UUID.randomUUID(), datasetId = UUID.randomUUID();


	private static Stream<Arguments> getProfilesList_params() {
		String uri = "/projects/%s/datasets/%s/profiles";
		Profile profile1 = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Profile profile2 = new Profile(projectId, datasetId, "2", "aka", new String[]{null, "2", null});
		List<Profile> profiles = new ArrayList<Profile>() {{add(profile1); add(profile2);}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("page", "0"),
				req2 = get(String.format(uri, projectId, datasetId)), req3 = get(String.format(uri, projectId, datasetId)).param("page", "-10");
		List<ProfileOutputModel> result1 = profiles.stream()
				.map(ProfileOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, profiles, HttpStatus.OK, result1,  null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, profiles, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getProfilesString_params() {
		String uri = "/projects/%s/datasets/%s/profiles";
		Schema schema1 = new Schema("taxon", "id", Method.MLST, "description", new String[]{"a", "b", "c"});
		Schema schema2 = new Schema("taxon", "id", Method.SNP, "description", new String[]{"a", "b", "c"});
		Profile profile1 = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Profile profile2 = new Profile(projectId, datasetId, "2", "aka", new String[]{null, "2", null});
		List<Profile> profiles = new ArrayList<Profile>() {{add(profile1); add(profile2);}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("page", "0");
		FileOutputModel result3 = new FileOutputModel(new MlFormatter().format(Collections.emptyList(), schema1));
		FileOutputModel result4 = new FileOutputModel(new MlFormatter().format(profiles, schema1));
		FileOutputModel result5 = new FileOutputModel(new SnpFormatter().format(profiles, schema2));
		return Stream.of(Arguments.of(req1, schema1, Collections.emptyList(), HttpStatus.OK, result3,  null),
				Arguments.of(req1, schema1, profiles, HttpStatus.OK, result4,  null),
				Arguments.of(req1, schema2, profiles, HttpStatus.OK, result5,  null));
	}

	private static Stream<Arguments> getProfile_params() {
		String uri = "/projects/%s/datasets/%s/profiles/%s";
		Profile profile1 = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Profile profile2 = new Profile(projectId, datasetId, "2", "aka", new String[]{null, "2", null});
		Profile.PrimaryKey key1 = profile1.getPrimaryKey();
		Profile.PrimaryKey key2 = profile2.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId, key1.getId())).param("version", "1"),
				req2 = get(String.format(uri, projectId, datasetId, key2.getId()));
		return Stream.of(Arguments.of(req1, profile1, HttpStatus.OK, new GetProfileOutputModel(profile1)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, profile2, HttpStatus.OK, new GetProfileOutputModel(profile2)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> saveProfile_params() {
		String uri = "/projects/%s/datasets/%s/profiles/%s";
		Profile profile = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Profile.PrimaryKey key = profile.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, projectId, datasetId, key.getId())),
				req2 = put(String.format(uri, projectId, datasetId, key.getId())).param("private_alleles", "true"),
				req3 = put(String.format(uri, projectId, datasetId, key.getId())).param("private_alleles", "test");
		ProfileInputModel input1 = new ProfileInputModel(key.getId(), "aka", new String[]{"1", "2", "3"}),
			input2 = new ProfileInputModel("different", "aka", null);
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req2, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req3, input1, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> putProfiles_params() {
		String uri = "/projects/%s/datasets/%s/profiles/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "b".getBytes());
		MockMultipartHttpServletRequestBuilder req1 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req2 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req3 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req4 = multipart(String.format(uri, projectId, datasetId));
		req1.with(r -> {r.setMethod(HttpMethod.PUT.name()); return r;});
		req2.with(r -> {r.setMethod(HttpMethod.PUT.name()); return r;});
		req3.with(r -> {r.setMethod(HttpMethod.PUT.name()); return r;}).param("private_alleles", "test");
		req4.with(r -> {r.setMethod(HttpMethod.PUT.name()); return r;});
		MockHttpServletRequestBuilder req5 = put(String.format(uri, taxonId, locusId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req2, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req2, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req5, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postProfiles_params() {
		String uri = "/projects/%s/datasets/%s/profiles/files";
		MockMultipartFile file = new MockMultipartFile("file", "", "text/plain", "bytes".getBytes());
		MockHttpServletRequestBuilder req1 = multipart(String.format(uri, projectId, datasetId)).file(file),
				req3 = multipart(String.format(uri, projectId, datasetId)),
				req4 = post(String.format(uri, taxonId, locusId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req3, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req4, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteProfile_params() {
		String uri = "/projects/%s/datasets/%s/profiles/%s";
		Profile profile = new Profile(projectId, datasetId, "1", "aka", new String[]{"1", "2", "3"});
		Profile.PrimaryKey key = profile.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, projectId, datasetId, key.getId()));
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
	@MethodSource("getProfilesList_params")
	public void getProfilesList(MockHttpServletRequestBuilder req, List<Profile> profiles, HttpStatus expectedStatus, List<ProfileOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Schema schema = new Schema("taxon", "schema", Method.MLVA, null, new String[] {"1", "2"});
		Pair<Schema, List<Profile>> pair = profiles == null ? null : new Pair<>(schema, profiles);
		Mockito.when(service.getProfiles(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(pair));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = http.parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if(expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getProject_id().toString(), p.get("project_id"));
					assertEquals(expectedResult.get(i).getDataset_id().toString(), p.get("dataset_id"));
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
					assertEquals(expectedResult.get(i).isDeprecated(), p.get("deprecated"));
				}
			}
		}
		else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getProfilesString_params")
	public void getProfilesString(MockHttpServletRequestBuilder req, Schema schema, List<Profile> profiles, HttpStatus expectedStatus, FileOutputModel expectedResult, ErrorOutputModel expectedError) throws Exception {
		Pair<Schema, List<Profile>> pair = profiles == null ? null : new Pair<>(schema, profiles);
		Mockito.when(service.getProfiles(any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(pair));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.TEXT_PLAIN);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult.toResponseEntity().getBody(), result.getContentAsString());
		else
			assertEquals(expectedError, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getProfile_params")
	public void getProfile(MockHttpServletRequestBuilder req, Profile profile, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.getProfile(any(), any(), anyString(), anyLong())).thenReturn(Optional.ofNullable(profile));
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, http.parseResult(GetProfileOutputModel.class, result));
		else
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("saveProfile_params")
	public void putProfile(MockHttpServletRequestBuilder req, ProfileInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if(input != null)
			Mockito.when(service.saveProfile(any(), anyBoolean())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putProfiles_params")
	public void putProfiles(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveProfilesOnConflictUpdate(any(), any(), anyBoolean(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postProfiles_params")
	public void postProfiles(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.saveProfilesOnConflictSkip(any(), any(), anyBoolean(), any())).thenReturn(ret);
		MockHttpServletResponse result = http.executeFileRequest(req);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteProfile_params")
	public void deleteProfile(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(service.deleteProfile(any(), any(), anyString())).thenReturn(ret);
		MockHttpServletResponse result = http.executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if(expectedStatus.is4xxClientError())
			assertEquals(expectedResult, http.parseResult(ErrorOutputModel.class, result));
	}

}
