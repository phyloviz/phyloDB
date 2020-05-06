package pt.ist.meic.phylodb.security.user;

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
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTests extends RepositoryTests {

	private static final int LIMIT = 2;
	private static final User first = new User("1one", "one", 1, false, Role.USER);
	private static final User second = new User("2two", "two", 1, false, Role.USER);
	private static final User[] state = new User[]{first, second};
	@Autowired
	private UserRepository repository;

	private static Stream<Arguments> findAll_params() {
		String email1 = "3test", provider1 = "3test", email3 = "5test", provider3 = "5provider";
		User first = new User(email1, provider1, 1, false, Role.USER), firstChanged = new User(email1, provider1, 2, false, Role.ADMIN),
				second = new User("4test", "4provider", 1, false, Role.USER),
				third = new User(email3, provider3, 1, false, Role.USER), thirdChanged = new User(email3, provider3, 2, false, Role.ADMIN),
				fourth = new User("6test", "6provider", 1, false, Role.USER);
		return Stream.of(Arguments.of(0, new User[0], new User[0]),
				Arguments.of(0, new User[]{state[0]}, new User[]{state[0]}),
				Arguments.of(0, new User[]{first, firstChanged}, new User[]{firstChanged}),
				Arguments.of(0, new User[]{state[0], state[1], first}, state),
				Arguments.of(0, new User[]{state[0], state[1], first, firstChanged}, state),
				Arguments.of(1, new User[0], new User[0]),
				Arguments.of(1, new User[]{state[0]}, new User[0]),
				Arguments.of(1, new User[]{first, firstChanged}, new User[0]),
				Arguments.of(1, new User[]{state[0], state[1], first}, new User[]{first}),
				Arguments.of(1, new User[]{state[0], state[1], first, firstChanged}, new User[]{firstChanged}),
				Arguments.of(1, new User[]{state[0], state[1], first, second}, new User[]{first, second}),
				Arguments.of(1, new User[]{state[0], state[1], first, firstChanged, second}, new User[]{firstChanged, second}),
				Arguments.of(1, new User[]{state[0], state[1], first, firstChanged}, new User[]{firstChanged}),
				Arguments.of(2, new User[0], new User[0]),
				Arguments.of(2, new User[]{state[0]}, new User[0]),
				Arguments.of(2, new User[]{first, firstChanged}, new User[0]),
				Arguments.of(2, new User[]{state[0], state[1], first, second, third}, new User[]{third}),
				Arguments.of(2, new User[]{state[0], state[1], first, second, third, thirdChanged}, new User[]{thirdChanged}),
				Arguments.of(2, new User[]{state[0], state[1], first, second, third, fourth}, new User[]{third, fourth}),
				Arguments.of(2, new User[]{state[0], state[1], first, second, third, thirdChanged, fourth}, new User[]{thirdChanged, fourth}),
				Arguments.of(-1, new User[0], new User[0]));
	}

	private static Stream<Arguments> find_params() {
		String email = "test", provider = "test";
		User.PrimaryKey key = new User.PrimaryKey(email, provider);
		User first = new User(email, provider, 1, false, Role.USER), second = new User(email, provider, 2, false, Role.ADMIN);
		return Stream.of(Arguments.of(key, 1, new User[0], null),
				Arguments.of(key, 1, new User[]{first}, first),
				Arguments.of(key, 2, new User[]{first, second}, second),
				Arguments.of(key, -3, new User[0], null),
				Arguments.of(key, 10, new User[]{first}, null),
				Arguments.of(key, -10, new User[]{first, second}, null),
				Arguments.of(null, 1, new User[0], null));
	}

	private static Stream<Arguments> exists_params() {
		String email = "test", provider = "test";
		User.PrimaryKey key = new User.PrimaryKey(email, provider);
		User first = new User(email, provider, 1, false, Role.USER),
				second = new User(email, provider, 1, true, Role.USER);
		return Stream.of(Arguments.of(key, new User[0], false),
				Arguments.of(key, new User[]{first}, true),
				Arguments.of(key, new User[]{second}, false),
				Arguments.of(null, new User[0], false));
	}

	private static Stream<Arguments> save_params() {
		String email = "3test", provider = "3test";
		User first = new User(email, provider, 1, false, Role.USER), second = new User(email, provider, 2, false, Role.ADMIN);
		return Stream.of(Arguments.of(first, new User[0], new User[]{state[0], state[1], first}, true, 2, 1),
				Arguments.of(second, new User[]{first}, new User[]{state[0], state[1], first, second}, true, 1, 1),
				Arguments.of(null, new User[0], state, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		String email = "3test", provider = "3test";
		User.PrimaryKey key = new User.PrimaryKey(email, provider);
		User first = new User(email, provider, 1, false, Role.USER), after = new User(email, provider, 1, true, Role.USER);
		return Stream.of(Arguments.of(key, new User[0], state, false),
				Arguments.of(key, new User[]{first}, new User[]{state[0], state[1], after}, true),
				Arguments.of(null, new User[0], state, false));
	}

	private static Stream<Arguments> anyMissing_params() {
		return Stream.of(Arguments.of(new User.PrimaryKey[]{state[0].getPrimaryKey()}, false),
				Arguments.of(new User.PrimaryKey[]{state[0].getPrimaryKey(), state[1].getPrimaryKey()}, false),
				Arguments.of(new User.PrimaryKey[]{state[0].getPrimaryKey(), new User.PrimaryKey("not", "not")}, true),
				Arguments.of(new User.PrimaryKey[]{new User.PrimaryKey("not", "not")}, true));
	}

	private void store(User[] users) {
		for (User user : users) {
			String statement = "MERGE (u:User {id: $, provider: $}) SET u.deprecated = $ WITH u\n" +
					"OPTIONAL MATCH (u)-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH u, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (u)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ud:UserDetails {role: $})";
			execute(new Query(statement, user.getPrimaryKey().getId(), user.getPrimaryKey().getProvider(), user.isDeprecated(), user.getRole().getName()));
		}
	}

	private User parse(Map<String, Object> row) {
		return new User((String) row.get("id"),
				(String) row.get("provider"),
				(Long) row.get("version"),
				(boolean) row.get("deprecated"),
				Role.valueOf(((String) row.get("role")).toUpperCase()));
	}

	private User[] findAll() {
		String statement = "MATCH (u:User)-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
				"RETURN u.id as id, u.provider as provider, u.deprecated as deprecated, r.version as version,\n" +
				"ud.role as role\n" +
				"ORDER BY id, version";
		Result result = query(new Query(statement));
		if (result == null) return new User[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(User[]::new);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, User[] state, User[] expected) {
		store(state);
		Optional<List<User>> result = repository.findAll(page, LIMIT);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<User> users = result.get();
		assertEquals(expected.length, users.size());
		assertArrayEquals(expected, users.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(User.PrimaryKey key, long version, User[] state, User expected) {
		store(UserRepositoryTests.state);
		store(state);
		Optional<User> result = repository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(User.PrimaryKey key, User[] state, boolean expected) {
		store(UserRepositoryTests.state);
		store(state);
		boolean result = repository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(User user, User[] state, User[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(UserRepositoryTests.state);
		store(state);
		Optional<QueryStatistics> result = repository.save(user);
		User[] stateResult = findAll();
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(User.PrimaryKey key, User[] state, User[] expectedState, boolean expectedResult) {
		store(UserRepositoryTests.state);
		store(state);
		boolean result = repository.remove(key);
		User[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("anyMissing_params")
	public void anyMissing(User.PrimaryKey[] keys, boolean expected) {
		store(UserRepositoryTests.state);
		boolean result = repository.anyMissing(keys);
		assertEquals(expected, result);
	}

}
