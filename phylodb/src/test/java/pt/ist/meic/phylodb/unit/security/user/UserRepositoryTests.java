package pt.ist.meic.phylodb.unit.security.user;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.unit.RepositoryTestsContext;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final User[] STATE = new User[]{USER1, USER2};

	private static Stream<Arguments> findAll_params() {
		String email1 = "3test", provider1 = "3test", email3 = "5test", provider3 = "5provider";
		User firstE = new User(email1, provider1, 1, false, Role.USER),
				firstChangedE = new User(email1, provider1, 2, false, Role.ADMIN),
				secondE = new User("4test", "4provider", 1, false, Role.USER),
				thirdE = new User(email3, provider3, 1, false, Role.USER),
				thirdChangedE = new User(email3, provider3, 2, false, Role.ADMIN),
				fourthE = new User("6test", "6provider", 1, false, Role.USER);
		VersionedEntity<User.PrimaryKey> first = new VersionedEntity<>(new User.PrimaryKey(email1, provider1), 1, false),
				firstChanged = new VersionedEntity<>(new User.PrimaryKey(email1, provider1), 2, false),
				second = new VersionedEntity<>(new User.PrimaryKey("4test", "4provider"), 1, false),
				third = new VersionedEntity<>(new User.PrimaryKey(email3, provider3), 1, false),
				thirdChanged = new VersionedEntity<>(new User.PrimaryKey(email3, provider3), 2, false),
				fourth = new VersionedEntity<>(new User.PrimaryKey("6test", "6provider"), 1, false),
				state0 = new VersionedEntity<>(STATE[0].getPrimaryKey(), STATE[0].getVersion(), STATE[0].isDeprecated()),
				state1 = new VersionedEntity<>(STATE[1].getPrimaryKey(), STATE[1].getVersion(), STATE[1].isDeprecated());
		return Stream.of(Arguments.of(0, new User[0], Collections.emptyList()),
				Arguments.of(0, new User[]{STATE[0]}, Collections.singletonList(state0)),
				Arguments.of(0, new User[]{firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(0, new User[]{STATE[0], STATE[1], firstE}, Arrays.asList(state0, state1)),
				Arguments.of(0, new User[]{STATE[0], STATE[1], firstE, firstChangedE}, Arrays.asList(state0, state1)),
				Arguments.of(1, new User[0], Collections.emptyList()),
				Arguments.of(1, new User[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(1, new User[]{firstE, firstChangedE}, Collections.emptyList()),
				Arguments.of(1, new User[]{STATE[0], STATE[1], firstE}, Collections.singletonList(first)),
				Arguments.of(1, new User[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(1, new User[]{STATE[0], STATE[1], firstE, secondE}, Arrays.asList(first, second)),
				Arguments.of(1, new User[]{STATE[0], STATE[1], firstE, firstChangedE, secondE}, Arrays.asList(firstChanged, second)),
				Arguments.of(1, new User[]{STATE[0], STATE[1], firstE, firstChangedE}, Collections.singletonList(firstChanged)),
				Arguments.of(2, new User[0], Collections.emptyList()),
				Arguments.of(2, new User[]{STATE[0]}, Collections.emptyList()),
				Arguments.of(2, new User[]{firstE, firstChangedE}, Collections.emptyList()),
				Arguments.of(2, new User[]{STATE[0], STATE[1], firstE, secondE, thirdE}, Collections.singletonList(third)),
				Arguments.of(2, new User[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE}, Collections.singletonList(thirdChanged)),
				Arguments.of(2, new User[]{STATE[0], STATE[1], firstE, secondE, thirdE, fourthE}, Arrays.asList(third, fourth)),
				Arguments.of(2, new User[]{STATE[0], STATE[1], firstE, secondE, thirdE, thirdChangedE, fourthE}, Arrays.asList(thirdChanged, fourth)),
				Arguments.of(-1, new User[0], Collections.emptyList()));
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
		return Stream.of(Arguments.of(first, new User[0], new User[]{STATE[0], STATE[1], first}, true, 2, 1),
				Arguments.of(second, new User[]{first}, new User[]{STATE[0], STATE[1], first, second}, true, 1, 1),
				Arguments.of(null, new User[0], STATE, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		String email = "3test", provider = "3test";
		User.PrimaryKey key = new User.PrimaryKey(email, provider);
		User first = new User(email, provider, 1, false, Role.USER), after = new User(email, provider, 1, true, Role.USER);
		return Stream.of(Arguments.of(key, new User[0], STATE, false),
				Arguments.of(key, new User[]{first}, new User[]{STATE[0], STATE[1], after}, true),
				Arguments.of(null, new User[0], STATE, false));
	}

	private static Stream<Arguments> anyMissing_params() {
		return Stream.of(Arguments.of(new User.PrimaryKey[]{STATE[0].getPrimaryKey()}, false),
				Arguments.of(new User.PrimaryKey[]{STATE[0].getPrimaryKey(), STATE[1].getPrimaryKey()}, false),
				Arguments.of(new User.PrimaryKey[]{STATE[0].getPrimaryKey(), new User.PrimaryKey("not", "not")}, true),
				Arguments.of(new User.PrimaryKey[]{new User.PrimaryKey("not", "not")}, true));
	}

	private void store(User[] users) {
		for (User user : users) {
			String statement = "MERGE (u:User {id: $, provider: $}) SET u.deprecated = $ WITH u\n" +
					"OPTIONAL MATCH (u)-[r:CONTAINS_DETAILS]->(ud:UserDetails)\n" +
					"WHERE r.to IS NULL SET r.to = datetime()\n" +
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
	public void findAll(int page, User[] state, List<VersionedEntity<User.PrimaryKey>> expected) {
		store(state);
		Optional<List<VersionedEntity<User.PrimaryKey>>> result = userRepository.findAllEntities(page, LIMIT);
		if (expected.size() == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<VersionedEntity<User.PrimaryKey>> users = result.get();
		assertEquals(expected.size(), users.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getPrimaryKey(), users.get(i).getPrimaryKey());
			assertEquals(expected.get(i).getVersion(), users.get(i).getVersion());
			assertEquals(expected.get(i).isDeprecated(), users.get(i).isDeprecated());
		}
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(User.PrimaryKey key, long version, User[] state, User expected) {
		store(UserRepositoryTests.STATE);
		store(state);
		Optional<User> result = userRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(User.PrimaryKey key, User[] state, boolean expected) {
		store(UserRepositoryTests.STATE);
		store(state);
		boolean result = userRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(User user, User[] state, User[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(UserRepositoryTests.STATE);
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = userRepository.save(user);
		User[] stateResult = findAll();
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(User.PrimaryKey key, User[] state, User[] expectedState, boolean expectedResult) {
		store(UserRepositoryTests.STATE);
		store(state);
		boolean result = userRepository.remove(key);
		User[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("anyMissing_params")
	public void anyMissing(User.PrimaryKey[] keys, boolean expected) {
		store(UserRepositoryTests.STATE);
		boolean result = userRepository.anyMissing(keys);
		assertEquals(expected, result);
	}

}
