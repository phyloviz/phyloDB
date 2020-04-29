package pt.ist.meic.phylodb.security.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ist.meic.phylodb.RepositoryTests;
import pt.ist.meic.phylodb.security.authentication.user.UserRepository;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectRepositoryTests extends RepositoryTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProjectRepository projectRepository;

	private static final User user1 = new User("1one", "one", 1, false, Role.USER);
	private static final User user2 = new User("2two", "two", 1, false, Role.USER);
	private static final Project[] projects = new Project[] {new Project(UUID.fromString("2023b71c-704f-425e-8dcf-b26fc84300e7"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
			new Project(UUID.fromString("26d20a45-470a-4336-81ab-ed057d3f5d66"), 1, false, "private1", "private", null, new User.PrimaryKey[]{user2.getPrimaryKey()}),
			new Project(UUID.fromString("3f809af7-2c99-43f7-b674-4843c77384c7"), 1, false,"private1", "public", null, new User.PrimaryKey[]{user2.getPrimaryKey()})};

	private void store(Project[] projects) {
		for (Project project : projects) {
			String statement = "MERGE (p:Project {id: $}) SET p.deprecated = $ WITH p\n" +
					"OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH p, COALESCE(r.version, 0) + 1 as v\n" +
					"CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProjectDetails {name: $, type: $, description: $}) WITH p, pd\n" +
					"CREATE (:Allele {deprecated: false})<-[:CONTAINS]-(p)-[:CONTAINS]->(d:Dataset {deprecated: false})-[:CONTAINS]->(:Profile {deprecated: false})\n" +
					"WITH pd\n";
			Query query = new Query(statement, project.getPrimaryKey(), project.isDeprecated(), project.getName(), project.getType(), project.getDescription());
			for (User.PrimaryKey u : project.getUsers()) {
				query.appendQuery("MATCH (u:User {id: $, provider: $}) WHERE u.deprecated = false CREATE (pd)-[:HAS]->(u) WITH pd\n");
				query.addParameter(u.getId(), u.getProvider());
			}
			query.subQuery(query.length() - "WITH pd\n".length());
			execute(query);
		}
	}

	private Project parse(Map<String, Object> row) {
		User.PrimaryKey[] userIds = Arrays.stream((Map<String, String>[]) row.get("users"))
				.map(a -> new User.PrimaryKey(a.get("id"), a.get("provider")))
				.toArray(User.PrimaryKey[]::new);
		return new Project(UUID.fromString((String) row.get("id")),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("name"),
				(String) row.get("type"),
				(String) row.get("description"),
				userIds);
	}

	private Project[] findAll() {
		String statement = "MATCH (p:Project)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)-[:HAS]->(u:User)\n" +
				"RETURN p.id as id, p.deprecated as deprecated, r.version as version, " +
				"pd.name as name, pd.type as type, pd.description as description, collect(DISTINCT {id : u.id, provider: u.provider}) as users\n" +
				"ORDER BY p.id, r.version";
		Result result = query(new Query(statement));
		if (result == null) return new Project[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Project[]::new);
	}

	private boolean hasDescendents(UUID key){
		String statement = "MATCH (p:Project {id: $})\n" +
				"OPTIONAL MATCH (p)-[:CONTAINS]->(a:Allele) WHERE a.deprecated = false WITH p, a\n" +
				"OPTIONAL MATCH (p)-[:CONTAINS]->(d:Dataset) WHERE d.deprecated = false WITH d, a\n" +
				"OPTIONAL MATCH (d)-[:CONTAINS]->(pf:Profile) WHERE pf.deprecated = false WITH d, a, pf\n" +
				"OPTIONAL MATCH (d)-[:CONTAINS]->(i:Isolate) WHERE i.deprecated = false = false WITH d, a, pf, i\n" +
				"RETURN (COUNT(a) + COUNT(d) + COUNT(pf) + COUNT(i)) <> 0";
		return query(Boolean.class, new Query(statement, key));
	}

	private static Stream<Arguments> findAll_params() {
		UUID key1 = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7"), key2 = UUID.fromString("5f809af7-2c99-43f7-b674-4843c77384c7"), key3 = UUID.fromString("8f809af7-2c99-43f7-b674-4843c77384c7");
		Project first = new Project(key1, 1, false, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				firstChanged = new Project(key1, 2, false, "name2", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				second = new Project(key2, 1, false, "name3", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				secondChanged = new Project(key2, 2, false, "name4", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				third = new Project(UUID.fromString("6f809af7-2c99-43f7-b674-4843c77384c7"), 1, false, "name5", "public", null, new User.PrimaryKey[]{user2.getPrimaryKey()}),
				fourth = new Project(UUID.fromString("7f809af7-2c99-43f7-b674-4843c77384c7"), 1, false, "name6", "public", null, new User.PrimaryKey[]{user2.getPrimaryKey()}),
				fifth = new Project(key3, 1, false, "name7", "public", null, new User.PrimaryKey[]{user2.getPrimaryKey()}),
				fifthChanged = new Project(key3, 2, false, "name77", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				sixth = new Project(UUID.fromString("9f809af7-2c99-43f7-b674-4843c77384c7"), 1, false, "name8", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()});
		return Stream.of(Arguments.of(0, new Project[0], new Project[0]),
				Arguments.of(0, new Project[]{projects[0]}, new Project[]{projects[0]}),
				Arguments.of(0, new Project[]{first, firstChanged}, new Project[]{firstChanged}),
				Arguments.of(0, new Project[]{projects[0], projects[1], projects[2], first}, new Project[]{projects[0], projects[2], first}),
				Arguments.of(0, new Project[]{projects[0], projects[1], projects[2], first, firstChanged}, new Project[]{projects[0], projects[2], firstChanged}),
				Arguments.of(1, new Project[0], new Project[0]),
				Arguments.of(1, new Project[]{projects[0]}, new Project[0]),
				Arguments.of(1, new Project[]{first, firstChanged}, new Project[0]),
				Arguments.of(1, new Project[]{projects[0], projects[1], projects[2], first, second}, new Project[]{second}),
				Arguments.of(1, new Project[]{projects[0], projects[1], projects[2], first, firstChanged, second, third}, new Project[]{second, third}),
				Arguments.of(1, new Project[]{projects[0], projects[1], projects[2], first, firstChanged, second, secondChanged, third}, new Project[]{secondChanged, third}),
				Arguments.of(2, new Project[0], new Project[0]),
				Arguments.of(2, new Project[]{projects[0]}, new Project[0]),
				Arguments.of(2, new Project[]{first, firstChanged}, new Project[0]),
				Arguments.of(2, new Project[]{projects[0], projects[1], projects[2], first, second, third, fourth, fifth}, new Project[]{fifth}),
				Arguments.of(2, new Project[]{projects[0], projects[1], projects[2], first, second, third, fourth, fifth, fifthChanged}, new Project[]{fifthChanged}),
				Arguments.of(2, new Project[]{projects[0], projects[1], projects[2], first, second, third, fourth, fifth, sixth}, new Project[]{fifth, sixth}),
				Arguments.of(2, new Project[]{projects[0], projects[1], projects[2], first, second, third, fourth, fifth, fifthChanged, sixth}, new Project[]{fifthChanged, sixth}),
				Arguments.of(-1, new Project[0], new Project[0]));
	}

	private static Stream<Arguments> find_params() {
		UUID key = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7");
		Project first = new Project(key, 1, false, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				second = new Project(key, 2, true, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()});
		return Stream.of(Arguments.of(key, 1, new Project[0], null),
				Arguments.of(key, 1, new Project[]{first}, first),
				Arguments.of(key, 2, new Project[]{first, second}, second),
				Arguments.of(key, -3, new Project[0], null),
				Arguments.of(key, 10, new Project[]{first}, null),
				Arguments.of(key, -10, new Project[]{first, second}, null),
				Arguments.of(null, 1, new Project[0], null));
	}

	private static Stream<Arguments> exists_params() {
		UUID key1 = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7");
		Project first = new Project(key1, 1, false, "name1", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				second = new Project(key1, 1, true, "name2", "private", null, new User.PrimaryKey[]{user2.getPrimaryKey()});
		return Stream.of(Arguments.of(key1, new Project[0], false),
				Arguments.of(key1, new Project[]{first}, true),
				Arguments.of(key1, new Project[]{second}, false),
				Arguments.of(null, new Project[0], false));
	}

	private static Stream<Arguments> save_params() {
		UUID id = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7");
		Project first = new Project(id, 1, false, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				second = new Project(id, 2, false, "name2", "private", "description", new User.PrimaryKey[]{user1.getPrimaryKey()});
		return Stream.of(Arguments.of(first, new Project[0], new Project[]{projects[0], projects[1], projects[2], first}, true, 2, 2),
				Arguments.of(second, new Project[]{first}, new Project[]{projects[0], projects[1], projects[2], first, second}, true, 1, 2),
				Arguments.of(null, new Project[0], new Project[]{projects[0], projects[1], projects[2]}, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		UUID key = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7");
		Project before = new Project(key, 1, false, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()}),
				after = new Project(key, 1, true, "name", "private", null, new User.PrimaryKey[]{user1.getPrimaryKey()});
		return Stream.of(Arguments.of(key, new Project[0], new Project[]{projects[0],  projects[1], projects[2]}, false),
				Arguments.of(key, new Project[]{before}, new Project[]{projects[0],  projects[1], projects[2], after}, true),
				Arguments.of(null, new Project[0], new Project[]{projects[0],  projects[1], projects[2]}, false));
	}

	@BeforeEach
	public void init() {
		for (User user : new User[]{user1, user2})
			userRepository.save(user);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Project[] state, Project[] expected) {
		store(state);
		Optional<List<Project>> result = projectRepository.findAll(page, 3, user1.getPrimaryKey());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Project> projects = result.get();
		assertEquals(expected.length,  projects.size());
		assertArrayEquals(expected, projects.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(UUID key, long version, Project[] state, Project expected) {
		store(ProjectRepositoryTests.projects);
		store(state);
		Optional<Project> result = projectRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(UUID key, Project[] state, boolean expected) {
		store(ProjectRepositoryTests.projects);
		store(state);
		boolean result = projectRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Project project, Project[] state, Project[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(ProjectRepositoryTests.projects);
		store(state);
		Optional<QueryStatistics> result = projectRepository.save(project);
		if(executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Project[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(UUID key, Project[] state, Project[] expectedState, boolean expectedResult) {
		store(ProjectRepositoryTests.projects);
		store(state);
		boolean result = projectRepository.remove(key);
		Project[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

}
