package pt.ist.meic.phylodb.unit.security.project;

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
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.model.GetProjectOutputModel;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.project.model.ProjectInputModel;
import pt.ist.meic.phylodb.security.project.model.ProjectOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ProjectControllerTests extends ControllerTestsContext {

	private static Stream<Arguments> getProjects_params() {
		String uri = "/projects";
		List<VersionedEntity<String>> projects = new ArrayList<VersionedEntity<String>>() {{
			add(new VersionedEntity<>(UUID.randomUUID().toString(), 1, false));
		}};
		MockHttpServletRequestBuilder req1 = get(uri).param("page", "0"),
				req2 = get(uri), req3 = get(uri).param("page", "-10");
		List<ProjectOutputModel> result = projects.stream()
				.map(ProjectOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, projects, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, projects, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getProject_params() {
		String uri = "/projects/%s";
		String id = UUID.randomUUID().toString();
		Project project = new Project(id, "x", Visibility.PRIVATE, "x", new User.PrimaryKey[]{new User.PrimaryKey("teste", "teste")});
		MockHttpServletRequestBuilder req1 = get(String.format(uri, id)).param("version", "1"),
				req2 = get(String.format(uri, id));
		return Stream.of(Arguments.of(req1, project, HttpStatus.OK, new GetProjectOutputModel(project)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, project, HttpStatus.OK, new GetProjectOutputModel(project)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> putProject_params() {
		String uri = "/projects/%s";
		String id = UUID.randomUUID().toString();
		Project project = new Project(id, "x", Visibility.PRIVATE, "x", new User.PrimaryKey[]{new User.PrimaryKey("teste", "teste")});
		MockHttpServletRequestBuilder req1 = put(String.format(uri, id));
		ProjectInputModel input1 = new ProjectInputModel(project.getPrimaryKey(), project.getName(), project.getVisibility().getName(), project.getDescription(), project.getUsers()),
				input2 = new ProjectInputModel(project.getPrimaryKey(), null, "error", project.getDescription(), null);
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postProject_params() {
		String uri = "/projects";
		MockHttpServletRequestBuilder req1 = post(uri);
		ProjectInputModel input1 = new ProjectInputModel(null, "x", "private", "description", new User.PrimaryKey[0]),
				input2 = new ProjectInputModel(null, null, "error", null, null);
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.CREATED, null),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteProject_params() {
		String uri = "/projects/%s";
		UUID id = UUID.randomUUID();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, id));
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
	@MethodSource("getProjects_params")
	public void getProjects(MockHttpServletRequestBuilder req, List<VersionedEntity<String>> projects, HttpStatus expectedStatus, List<ProjectOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(projectService.getProjects(any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(projects));
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
	@MethodSource("getProject_params")
	public void getProject(MockHttpServletRequestBuilder req, Project project, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(projectService.getProject(any(), anyLong())).thenReturn(Optional.ofNullable(project));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetProjectOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putProject_params")
	public void updateProject(MockHttpServletRequestBuilder req, ProjectInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(projectService.saveProject(any(), any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postProject_params")
	public void postProject(MockHttpServletRequestBuilder req, ProjectInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		if (input != null)
			Mockito.when(projectService.saveProject(any(), any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			CreatedOutputModel parsed = parseResult(CreatedOutputModel.class, result);
			assertNotNull(parsed.getId());
		} else if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteProject_params")
	public void deleteProject(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(projectService.deleteProject(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
