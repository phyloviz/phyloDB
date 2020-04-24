package pt.ist.meic.phylodb.security.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.neo4j.ogm.model.QueryStatistics;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ist.meic.phylodb.Test;
import pt.ist.meic.phylodb.security.authentication.user.UserRepository;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.security.authorization.project.ProjectService;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.utils.MockResult;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class ProjectServiceTests extends Test {

	@MockBean
	private UserRepository userRepository;
	@MockBean
	private ProjectRepository projectRepository;
	@InjectMocks
	private ProjectService service;

	private static final int LIMIT = 2;
	private static final User user1 = new User("1one", "one", 1, false, Role.USER);
	private static final User user2 = new User("2two", "two", 1, false, Role.USER);
	private static final Project[] state = new Project[] {new Project(UUID.fromString("2023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
			new Project(UUID.fromString("26d20a45-470a-4336-81ab-ed057d3f5d66"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user2.getPrimaryKey()}),
			new Project(UUID.fromString("3f809af7-2c99-43f7-b674-4843c77384c7"), 1, false,"private1", "public", null, new User.PrimaryKey[]{user2.getPrimaryKey()})};

	private static Stream<Arguments> getProjects_params() {
		List<Project> expected1 = new ArrayList<Project>() {{ add(state[0]); }};
		List<Project> expected2 = new ArrayList<Project>() {{ add(state[0]); add(state[1]); }};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getProject_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), 1, state[0]),
				Arguments.of(state[1].getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveProject_params() {
		return Stream.of(Arguments.of(state[0], user1.getPrimaryKey(), false, new MockResult().queryStatistics()),
				Arguments.of(state[1], user2.getPrimaryKey(), true, null),
				Arguments.of(null, user2.getPrimaryKey(), false, null));
	}

	private static Stream<Arguments> deleteProject_params() {
		return Stream.of(Arguments.of(state[0].getPrimaryKey(), true),
				Arguments.of(state[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getProjects_params")
	public void getProjects(int page, List<Project> expected) {
		Mockito.when(projectRepository.findAll(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<Project>> result = service.getProjects(user1.getPrimaryKey(), page, LIMIT);
		if(expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<Project> users = result.get();
		assertEquals(expected.size(), users.size());
		assertEquals(expected, users);
	}

	@ParameterizedTest
	@MethodSource("getProject_params")
	public void getProject(UUID key, long version, Project expected) {
		Mockito.when(projectRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Project> result = service.getProject(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveProject_params")
	public void saveProject(Project project, User.PrimaryKey user, boolean missing, QueryStatistics expected) {
		Mockito.when(userRepository.anyMissing(any())).thenReturn(missing);
		Mockito.when(projectRepository.save(any())).thenReturn(Optional.ofNullable(expected));
		boolean result = service.saveProject(project, user);
		assertEquals(expected != null, result);
	}

	@ParameterizedTest
	@MethodSource("deleteProject_params")
	public void deleteProject(UUID key, boolean expected) {
		Mockito.when(projectRepository.remove(any())).thenReturn(expected);
		boolean result = service.deleteProject(key);
		assertEquals(expected, result);
	}

}
