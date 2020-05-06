package pt.ist.meic.phylodb.typing.isolate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class IsolateRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	protected static final Isolate[] STATE = new Isolate[]{ISOLATE1, ISOLATE2};

	private static Stream<Arguments> findAll_params() {
		Isolate first = isolate("3", 1, false, null, new Ancillary[] {ANCILLARY1}, PROFILE1),
				firstChanged = isolate("3", 2, false, null, new Ancillary[] {ANCILLARY2}, PROFILE1),
				second = isolate("4", 1, false, null, new Ancillary[] {ANCILLARY1, ANCILLARY2}, null),
				third = isolate("5", 1, false, null, new Ancillary[0], null),
				thirdChanged = isolate("5", 2, false, null, new Ancillary[] {ANCILLARY1, ANCILLARY2, new Ancillary("key3", "value3")}, PROFILE2),
				fourth = isolate("6", 1, false, null, new Ancillary[0], PROFILE1);
		return Stream.of(Arguments.of(0, new Isolate[0], new Isolate[0]),
				Arguments.of(0, new Isolate[]{STATE[0]}, new Isolate[]{STATE[0]}),
				Arguments.of(0, new Isolate[]{first, firstChanged}, new Isolate[]{firstChanged}),
				Arguments.of(0, new Isolate[]{STATE[0], STATE[1], first}, STATE),
				Arguments.of(0, new Isolate[]{STATE[0], STATE[1], first, firstChanged}, STATE),
				Arguments.of(1, new Isolate[0], new Isolate[0]),
				Arguments.of(1, new Isolate[]{STATE[0]}, new Isolate[0]),
				Arguments.of(1, new Isolate[]{first, firstChanged}, new Isolate[0]),
				Arguments.of(1, new Isolate[]{STATE[0], STATE[1], first}, new Isolate[]{first}),
				Arguments.of(1, new Isolate[]{STATE[0], STATE[1], first, firstChanged}, new Isolate[]{firstChanged}),
				Arguments.of(1, new Isolate[]{STATE[0], STATE[1], first, second}, new Isolate[]{first, second}),
				Arguments.of(1, new Isolate[]{STATE[0], STATE[1], first, firstChanged, second}, new Isolate[]{firstChanged, second}),
				Arguments.of(1, new Isolate[]{STATE[0], STATE[1], first, firstChanged}, new Isolate[]{firstChanged}),
				Arguments.of(2, new Isolate[0], new Isolate[0]),
				Arguments.of(2, new Isolate[]{STATE[0]}, new Isolate[0]),
				Arguments.of(2, new Isolate[]{first, firstChanged}, new Isolate[0]),
				Arguments.of(2, new Isolate[]{STATE[0], STATE[1], first, second, third}, new Isolate[]{third}),
				Arguments.of(2, new Isolate[]{STATE[0], STATE[1], first, second, third, thirdChanged}, new Isolate[]{thirdChanged}),
				Arguments.of(2, new Isolate[]{STATE[0], STATE[1], first, second, third, fourth}, new Isolate[]{third, fourth}),
				Arguments.of(2, new Isolate[]{STATE[0], STATE[1], first, second, third, thirdChanged, fourth}, new Isolate[]{thirdChanged, fourth}),
				Arguments.of(-1, new Isolate[0], new Isolate[0]));
	}

	private static Stream<Arguments> find_params() {
		Ancillary[] ancillary1 = new Ancillary[] {IsolateRepositoryTests.ANCILLARY1};
		Ancillary[] ancillaryN = new Ancillary[] {IsolateRepositoryTests.ANCILLARY1, ANCILLARY2};
		Isolate.PrimaryKey key = new Isolate.PrimaryKey(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3");
		Isolate first = isolate(key.getId(), 1, false, "teste", ancillary1, null),
				second = isolate(key.getId(), 1, false, null, ancillary1, PROFILE1),
				third = isolate(key.getId(), 1, false, "test", ancillaryN, null),
				fourth = isolate(key.getId(), 1, false, null, ancillaryN, PROFILE1),
				fifth = isolate(key.getId(), 2, false, "changed1", ancillary1, null),
				sixth = isolate(key.getId(), 2, false, "changed2", ancillary1, PROFILE1),
				seventh = isolate(key.getId(), 2, false, "changed3", ancillaryN, null),
				eight = isolate(key.getId(), 2, false, "changed5", ancillaryN, PROFILE1);
		return Stream.of(Arguments.of(key, 1, new Isolate[0], null),
				Arguments.of(key, 1, new Isolate[]{first}, first),
				Arguments.of(key, 1, new Isolate[]{second}, second),
				Arguments.of(key, 1, new Isolate[]{third}, third),
				Arguments.of(key, 1, new Isolate[]{fourth}, fourth),
				Arguments.of(key, 2, new Isolate[]{first, fifth}, fifth),
				Arguments.of(key, 2, new Isolate[]{second, sixth}, sixth),
				Arguments.of(key, 2, new Isolate[]{third, seventh}, seventh),
				Arguments.of(key, 2, new Isolate[]{fourth, eight}, eight),
				Arguments.of(key, -11, new Isolate[]{first}, null),
				Arguments.of(key, 20, new Isolate[]{third, seventh}, null),
				Arguments.of(null, 1, new Isolate[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Ancillary[] ancillary1 = new Ancillary[] {IsolateRepositoryTests.ANCILLARY1};
		Ancillary[] ancillaryN = new Ancillary[] {IsolateRepositoryTests.ANCILLARY1, ANCILLARY2};
		Isolate.PrimaryKey key = new Isolate.PrimaryKey(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3");
		Isolate first = isolate(key.getId(), 1, false, null, ancillary1, PROFILE1),
				second = isolate(key.getId(), 1, true, null, ancillary1, null),
				third = isolate(key.getId(), 1, false, null, ancillaryN, null),
				fourth = isolate(key.getId(), 1, true, null, ancillaryN, PROFILE1);
		return Stream.of(Arguments.of(key, new Isolate[0], false),
				Arguments.of(key, new Isolate[]{first}, true),
				Arguments.of(key, new Isolate[]{second}, false),
				Arguments.of(key, new Isolate[]{third}, true),
				Arguments.of(key, new Isolate[]{fourth}, false),
				Arguments.of(null, new Isolate[0], false));
	}

	private static Stream<Arguments> save_params() {
		Isolate.PrimaryKey key = new Isolate.PrimaryKey(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3");
		Ancillary notExists1 = new Ancillary("new1", "new1"), notExists2 = new Ancillary("new2", "new2");
		Isolate first = isolate(key.getId(), 1, false, "test", new Ancillary[0], null),
				second = isolate(key.getId(), 1, false, null, new Ancillary[0], PROFILE1),
				third = isolate(key.getId(), 1, false, "description", new Ancillary[]{notExists1}, null),
				fourth = isolate(key.getId(), 1, false, "description", new Ancillary[]{ANCILLARY1}, null),
				fifth = isolate(key.getId(), 1, false, null, new Ancillary[]{notExists1}, PROFILE1),
				sixth = isolate(key.getId(), 1, false, null, new Ancillary[]{ANCILLARY1}, PROFILE1),
				seventh = isolate(key.getId(), 1, false, "teste", new Ancillary[]{notExists1, notExists2}, null),
				eighth = isolate(key.getId(), 1, false, "teste", new Ancillary[]{notExists1, notExists2}, PROFILE1),
				nineth = isolate(key.getId(), 1, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null),
				tenth = isolate(key.getId(), 1, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE1),
				firstV2 = isolate(key.getId(), 2, false, "testNew", new Ancillary[0], null),
				secondV2 = isolate(key.getId(), 2, false, "new", new Ancillary[0], PROFILE1),
				thirdV2 = isolate(key.getId(), 2, false, "description2", new Ancillary[]{notExists2}, null),
				fourthV2 = isolate(key.getId(), 2, false, "description2", new Ancillary[]{ANCILLARY1}, null),
				fifthV2 = isolate(key.getId(), 2, false, "different2", new Ancillary[]{notExists2}, PROFILE1),
				sixthV2 = isolate(key.getId(), 2, false, "different2", new Ancillary[]{ANCILLARY1}, PROFILE1),
				seventhV2 = isolate(key.getId(), 2, false, "teste", new Ancillary[]{new Ancillary("k1", "v1"), new Ancillary("k2", "v2")}, null),
				eightV2 = isolate(key.getId(), 2, false, "teste", new Ancillary[]{new Ancillary("k1", "v1"), new Ancillary("k2", "v2")}, PROFILE1),
				ninethV2 = isolate(key.getId(), 2, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null),
				tenthV2 = isolate(key.getId(), 2, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE1);
		return Stream.of(Arguments.of(first, new Isolate[0], new Isolate[]{STATE[0], STATE[1], first}, true, 2, 2),
				Arguments.of(second, new Isolate[0], new Isolate[]{STATE[0], STATE[1], second}, true, 2, 3),
				Arguments.of(third, new Isolate[0], new Isolate[]{STATE[0], STATE[1], third}, true, 3, 3),
				Arguments.of(fourth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], fourth}, true, 2, 3),
				Arguments.of(fifth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], fifth}, true, 3, 4),
				Arguments.of(sixth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], sixth}, true, 2, 4),
				Arguments.of(seventh, new Isolate[0], new Isolate[]{STATE[0], STATE[1], seventh}, true, 4, 4),
				Arguments.of(eighth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], eighth}, true, 4, 5),
				Arguments.of(nineth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], nineth}, true, 2, 4),
				Arguments.of(tenth, new Isolate[0], new Isolate[]{STATE[0], STATE[1], tenth}, true, 2, 5),
				Arguments.of(firstV2, new Isolate[] {first}, new Isolate[]{STATE[0], STATE[1], first, firstV2}, true, 1, 1),
				Arguments.of(secondV2, new Isolate[] {second}, new Isolate[]{STATE[0], STATE[1], second, secondV2}, true, 1, 2),
				Arguments.of(thirdV2, new Isolate[] {third}, new Isolate[]{STATE[0], STATE[1], third, thirdV2}, true, 2, 2),
				Arguments.of(fourthV2, new Isolate[] {fourth}, new Isolate[]{STATE[0], STATE[1], fourth, fourthV2}, true, 1, 2),
				Arguments.of(fifthV2, new Isolate[] {fifth}, new Isolate[]{STATE[0], STATE[1], fifth, fifthV2}, true, 2, 3),
				Arguments.of(sixthV2, new Isolate[] {sixth}, new Isolate[]{STATE[0], STATE[1], sixth, sixthV2}, true, 1, 3),
				Arguments.of(seventhV2, new Isolate[] {seventh}, new Isolate[]{STATE[0], STATE[1], seventh, seventhV2}, true, 3, 3),
				Arguments.of(eightV2, new Isolate[] {eighth}, new Isolate[]{STATE[0], STATE[1], eighth, eightV2}, true, 3, 4),
				Arguments.of(ninethV2, new Isolate[] {nineth}, new Isolate[]{STATE[0], STATE[1], nineth, ninethV2}, true, 1, 3),
				Arguments.of(tenthV2, new Isolate[] {tenth}, new Isolate[]{STATE[0], STATE[1], tenth, tenthV2}, true, 1, 4),
				Arguments.of(null, new Isolate[0], STATE, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Isolate.PrimaryKey key = new Isolate.PrimaryKey(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3");
		Ancillary[] ancillaryN = new Ancillary[] {ANCILLARY1, ANCILLARY2};
		Isolate first = isolate(key.getId(), 1, false, null, ancillaryN, PROFILE1),
				firstDeleted = isolate(key.getId(), 1, true, null, ancillaryN, PROFILE1);
		return Stream.of(Arguments.of(key, new Isolate[0], STATE, false),
				Arguments.of(key, new Isolate[]{first}, new Isolate[]{STATE[0], STATE[1], firstDeleted}, true),
				Arguments.of(null, new Isolate[0], STATE, false));
	}

	private static Stream<Arguments> saveAll_params() {
		Isolate.PrimaryKey key = new Isolate.PrimaryKey(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "3");
		String key1 = "4", key2 = "5", key3 = "6", key4 = "7", key5 = "9";
		Ancillary notExists1 = new Ancillary("new1", "new1"), notExists2 = new Ancillary("new2", "new2");
		Isolate first = isolate(key.getId(), 1, false, "test", new Ancillary[0], null),
				second = isolate(key1, 1, false, null, new Ancillary[0], PROFILE1),
				third = isolate(key2, 1, false, "teste", new Ancillary[]{notExists1, notExists2}, null),
				fourth = isolate(key3, 1, false, "teste", new Ancillary[]{notExists1, notExists2}, PROFILE1),
				fifth = isolate(key4, 1, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null),
				sixth = isolate(key5, 1, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE1),
				firstV2 = isolate(key.getId(), 2, false, "testNew", new Ancillary[0], null),
				secondV2 = isolate(key1, 2, false, "new", new Ancillary[0], PROFILE1),
				thirdV2 = isolate(key2, 2, false, "teste", new Ancillary[]{new Ancillary("k1", "v1"), new Ancillary("k2", "v2")}, null),
				fourthV2 = isolate(key3, 2, false, "teste", new Ancillary[]{new Ancillary("k3", "v3"), new Ancillary("k4", "v4")}, PROFILE1),
				fifthV2 = isolate(key4, 2, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, null),
				sixthV2 = isolate(key5, 2, false, "empty", new Ancillary[]{ANCILLARY1, ANCILLARY2}, PROFILE1);
		return Stream.of(Arguments.of(Collections.emptyList(), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1]}, false, 0, 0),
				Arguments.of(Collections.singletonList(first), new Isolate[]{STATE[1]}, new Isolate[]{STATE[1], first}, true, 2, 2),
				Arguments.of(Collections.singletonList(firstV2), new Isolate[]{first}, new Isolate[]{first, firstV2}, true, 1, 1),
				Arguments.of(Collections.singletonList(second), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1], second}, true, 2, 3),
				Arguments.of(Collections.singletonList(secondV2), new Isolate[]{STATE[0], STATE[1], second}, new Isolate[]{STATE[0], STATE[1], second, secondV2}, true, 1, 2),
				Arguments.of(Collections.singletonList(third), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1], third}, true, 4, 4),
				Arguments.of(Collections.singletonList(thirdV2), new Isolate[]{STATE[0], STATE[1], third}, new Isolate[]{STATE[0], STATE[1], third, thirdV2}, true, 3, 3),
				Arguments.of(Collections.singletonList(fourth), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1], fourth}, true, 4, 5),
				Arguments.of(Collections.singletonList(fourthV2), new Isolate[]{STATE[0], STATE[1], fourth}, new Isolate[]{STATE[0], STATE[1], fourth, fourthV2}, true, 3, 4),
				Arguments.of(Collections.singletonList(fifth), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1], fifth}, true, 2, 4),
				Arguments.of(Collections.singletonList(fifthV2), new Isolate[]{STATE[0], STATE[1], fifth}, new Isolate[]{STATE[0], STATE[1], fifth, fifthV2}, true, 1, 3),
				Arguments.of(Collections.singletonList(sixth), new Isolate[]{STATE[0], STATE[1]}, new Isolate[]{STATE[0], STATE[1], sixth}, true, 2, 5),
				Arguments.of(Collections.singletonList(sixthV2), new Isolate[]{STATE[0], STATE[1], sixth}, new Isolate[]{STATE[0], STATE[1], sixth, sixthV2}, true, 1, 4),
				Arguments.of(Arrays.asList(first, second), new Isolate[]{STATE[0]}, new Isolate[]{STATE[0], first, second}, true, 4, 5),
				Arguments.of(Arrays.asList(firstV2, secondV2), new Isolate[]{STATE[0], first, second}, new Isolate[]{STATE[0], first, firstV2, second, secondV2}, true, 2, 3),
				Arguments.of(Arrays.asList(third, fourth), new Isolate[]{STATE[0]}, new Isolate[]{STATE[0], third, fourth}, true, 6, 9),
				Arguments.of(Arrays.asList(thirdV2, fourthV2), new Isolate[]{STATE[0], third, fourth}, new Isolate[]{STATE[0], third, thirdV2, fourth, fourthV2}, true, 6, 7),
				Arguments.of(Arrays.asList(fifth, sixth), new Isolate[]{STATE[0]}, new Isolate[]{STATE[0], fifth, sixth}, true, 4, 9),
				Arguments.of(Arrays.asList(fifthV2, sixthV2), new Isolate[]{STATE[0], fifth, sixth}, new Isolate[]{STATE[0], fifth, fifthV2, sixth, sixthV2}, true, 2, 7));
	}

	private void store(Isolate[] isolates) {
		for (Isolate isolate : isolates) {
			Isolate.PrimaryKey key = isolate.getPrimaryKey();
			Query query = new Query("MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $}) WHERE p.deprecated = false AND d.deprecated = false\n", key.getProjectId(), key.getDatasetId());
			String statement = "MERGE (d)-[:CONTAINS]->(i:Isolate {id: $}) SET i.deprecated = $ WITH d, i\n" +
					"OPTIONAL MATCH (i)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH d, i, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (i)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(id:IsolateDetails {description: $})\n";
			query.appendQuery(statement).addParameter(key.getId(), isolate.isDeprecated(), isolate.getDescription());
			if (isolate.getProfile() != null) {
				statement = "WITH d, id\n" +
						"MATCH (d)-[:CONTAINS]->(p:Profile {id: $})-[r:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
						"WHERE p.deprecated = false AND NOT EXISTS(r.to)\n" +
						"CREATE (id)-[:HAS {version: r.version}]->(p)\n";
				query.appendQuery(statement).addParameter(isolate.getProfile().getPrimaryKey().getId());
			}
			query.appendQuery("WITH d, id\n");
			Ancillary[] ancillaries = isolate.getAncillaries();
			for (Ancillary ancillary : ancillaries) {
				query.appendQuery("MERGE (a:Ancillary {key: $, value: $}) WITH d, id, a\n")
						.appendQuery("CREATE (id)-[:HAS]->(a) WITH d, id\n")
						.addParameter(ancillary.getKey(), ancillary.getValue());
			}
			query.subQuery(query.length() - "WITH d, id\n".length());
			execute(query);
		}
	}

	private Isolate parse(Map<String, Object> row) {
		UUID projectId = UUID.fromString(row.get("projectId").toString()), datasetId = UUID.fromString(row.get("datasetId").toString());
		Entity<Profile.PrimaryKey> profile = null;
		if (row.get("profileId") != null)
			profile = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, (String) row.get("profileId")), (long) row.get("profileVersion"), (boolean) row.get("profileDeprecated"));
		Ancillary[] ancillaries = Arrays.stream((Map<String, Object>[]) row.get("ancillaries"))
				.filter(a -> a.get("key") != null)
				.map(a -> new Ancillary((String) a.get("key"), (String) a.get("value")))
				.toArray(Ancillary[]::new);
		return new Isolate(projectId,
				datasetId,
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"),
				ancillaries,
				profile);
	}

	private Isolate[] findAll() {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})-[:CONTAINS]->(i:Isolate)-[r:CONTAINS_DETAILS]->(id:IsolateDetails)\n" +
				"OPTIONAL MATCH (id)-[h:HAS]->(p:Profile)\n" +
				"OPTIONAL MATCH (id)-[:HAS]->(a:Ancillary)\n" +
				"WITH pj, d, r, i, id, h, p, a\n" +
				"ORDER BY pj.id, d.id, i.id, r.version, a.key\n" +
				"WITH pj, d, r, i, id, h, p, collect(DISTINCT {key: a.key, value: a.value}) as ancillary\n" +
				"RETURN pj.id as projectId, d.id as datasetId, i.id as id, i.deprecated as deprecated, r.version as version, " +
				"p.id as profileId, p.deprecated as profileDeprecated, h.version as profileVersion, " +
				"id.description as description, ancillary as ancillaries";
		Result result = query(new Query(statement, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId()));
		if (result == null) return new Isolate[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Isolate[]::new);
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(TAXON1);
		locusRepository.save(LOCUS1);
		locusRepository.save(LOCUS2);
		userRepository.save(USER1);
		projectRepository.save(PROJECT1);
		projectRepository.save(PROJECT2);
		schemaRepository.save(SCHEMA1);
		datasetRepository.save(DATASET1);
		alleleRepository.save(ALLELE11P);
		alleleRepository.save(ALLELE12);
		alleleRepository.save(ALLELE21);
		alleleRepository.save(ALLELE22);
		profileRepository.save(PROFILE1);
		profileRepository.save(PROFILE2);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Isolate[] state, Isolate[] expected) {
		store(state);
		Optional<List<Isolate>> result = isolateRepository.findAll(page, LIMIT, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Isolate> isolates = result.get();
		assertEquals(expected.length, isolates.size());
		assertArrayEquals(expected, isolates.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Isolate.PrimaryKey key, long version, Isolate[] state, Isolate expected) {
		store(IsolateRepositoryTests.STATE);
		store(state);
		Optional<Isolate> result = isolateRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Isolate.PrimaryKey key, Isolate[] state, boolean expected) {
		store(IsolateRepositoryTests.STATE);
		store(state);
		boolean result = isolateRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Isolate profile, Isolate[] state, Isolate[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(IsolateRepositoryTests.STATE);
		store(state);
		Optional<QueryStatistics> result = isolateRepository.save(profile);
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Isolate[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Isolate.PrimaryKey key, Isolate[] state, Isolate[] expectedState, boolean expectedResult) {
		store(IsolateRepositoryTests.STATE);
		store(state);
		boolean result = isolateRepository.remove(key);
		Isolate[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAll(List<Isolate> isolates, Isolate[] state, Isolate[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(state);
		Optional<QueryStatistics> result = isolateRepository.saveAll(isolates, PROJECT1.getPrimaryKey().toString(), DATASET1.getPrimaryKey().getId().toString());
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());

		Isolate[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

}
