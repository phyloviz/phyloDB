package pt.ist.meic.phylodb.unit.security.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.unit.ServiceTestsContext;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class ProjectServiceTests extends ServiceTestsContext {

	private static final int LIMIT = 2;
	private static final Project[] STATE = new Project[]{PROJECT1, new Project("26d20a45-470a-4336-81ab-ed057d3f5d66", 1, false, "private1", Visibility.PRIVATE, null, new User.PrimaryKey[]{USER2.getPrimaryKey()}),
			new Project("3f809af7-2c99-43f7-b674-4843c77384c7", 1, false, "private1", Visibility.PUBLIC, null, new User.PrimaryKey[]{USER2.getPrimaryKey()})};

	private static Stream<Arguments> getProjects_params() {
		VersionedEntity<String> state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		List<VersionedEntity<String>> expected1 = new ArrayList<VersionedEntity<String>>() {{
			add(state0);
		}};
		List<VersionedEntity<String>> expected2 = new ArrayList<VersionedEntity<String>>() {{
			add(state0);
			add(state1);
		}};
		return Stream.of(Arguments.of(0, Collections.emptyList()),
				Arguments.of(0, expected1),
				Arguments.of(0, expected2),
				Arguments.of(-1, null));
	}

	private static Stream<Arguments> getProject_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), 1, STATE[0]),
				Arguments.of(STATE[1].getPrimaryKey(), 1, null));
	}

	private static Stream<Arguments> saveProject_params() {
		return Stream.of(Arguments.of(STATE[0], USER1.getPrimaryKey(), false, true),
				Arguments.of(STATE[1], USER2.getPrimaryKey(), true, false),
				Arguments.of(null, USER2.getPrimaryKey(), false, false));
	}

	private static Stream<Arguments> deleteProject_params() {
		return Stream.of(Arguments.of(STATE[0].getPrimaryKey(), true),
				Arguments.of(STATE[0].getPrimaryKey(), false));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@ParameterizedTest
	@MethodSource("getProjects_params")
	public void getProjects(int page, List<VersionedEntity<String>> expected) {
		Mockito.when(projectRepository.findAllEntities(anyInt(), anyInt(), any())).thenReturn(Optional.ofNullable(expected));
		Optional<List<VersionedEntity<String>>> result = projectService.getProjects(USER1.getPrimaryKey().getId(), USER1.getPrimaryKey().getProvider(), page, LIMIT);
		if (expected == null && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertNotNull(expected);
		assertTrue(result.isPresent());
		List<VersionedEntity<String>> projects = result.get();
		assertEquals(expected.size(), projects.size());
		assertEquals(expected, projects);
	}

	@ParameterizedTest
	@MethodSource("getProject_params")
	public void getProject(String key, long version, Project expected) {
		Mockito.when(projectRepository.find(any(), anyLong())).thenReturn(Optional.ofNullable(expected));
		Optional<Project> result = projectService.getProject(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("saveProject_params")
	public void saveProject(Project project, User.PrimaryKey user, boolean missing, boolean expected) {
		Mockito.when(userRepository.anyMissing(any())).thenReturn(missing);
		Mockito.when(projectRepository.save(any())).thenReturn(expected);
		boolean result = projectService.saveProject(project, user);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("deleteProject_params")
	public void deleteProject(String key, boolean expected) {
		Mockito.when(projectRepository.remove(any())).thenReturn(expected);
		boolean result = projectService.deleteProject(key);
		assertEquals(expected, result);
	}

}
