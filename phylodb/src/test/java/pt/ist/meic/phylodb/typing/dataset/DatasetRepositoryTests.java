package pt.ist.meic.phylodb.typing.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class DatasetRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Dataset[] STATE = new Dataset[]{DATASET1, DATASET2};

	private static Stream<Arguments> findAll_params() {
		UUID key1 = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7"), key2 = UUID.fromString("6f809af7-2c99-43f7-b674-4843c77384c7");
		Entity<Schema.PrimaryKey> s1 = new Entity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated()),
				s2 = new Entity<>(SCHEMA2.getPrimaryKey(), SCHEMA2.getVersion(), SCHEMA2.isDeprecated());
		Dataset first = new Dataset(PROJECT1.getPrimaryKey(), key1, 1, false, "name", s1),
				firstChanged = new Dataset(PROJECT1.getPrimaryKey(), key1, 2, false, "name2", s2),
				second = new Dataset(PROJECT1.getPrimaryKey(), UUID.fromString("5f809af7-2c99-43f7-b674-4843c77384c7"), 1, false, "name3", s2),
				third = new Dataset(PROJECT1.getPrimaryKey(), key2, 1, false, "name5", s2),
				thirdChanged = new Dataset(PROJECT1.getPrimaryKey(), key2, 2, false, "name4", s1),
				fourth = new Dataset(PROJECT1.getPrimaryKey(), UUID.fromString("7f809af7-2c99-43f7-b674-4843c77384c7"), 1, false, "name6", s1);
		return Stream.of(Arguments.of(0, new Dataset[0], new Dataset[0]),
				Arguments.of(0, new Dataset[]{STATE[0]}, new Dataset[]{STATE[0]}),
				Arguments.of(0, new Dataset[]{first, firstChanged}, new Dataset[]{firstChanged}),
				Arguments.of(0, new Dataset[]{STATE[0], STATE[1], first}, STATE),
				Arguments.of(0, new Dataset[]{STATE[0], STATE[1], first, firstChanged}, STATE),
				Arguments.of(1, new Dataset[0], new Dataset[0]),
				Arguments.of(1, new Dataset[]{STATE[0]}, new Dataset[0]),
				Arguments.of(1, new Dataset[]{first, firstChanged}, new Dataset[0]),
				Arguments.of(1, new Dataset[]{STATE[0], STATE[1], first}, new Dataset[]{first}),
				Arguments.of(1, new Dataset[]{STATE[0], STATE[1], first, firstChanged}, new Dataset[]{firstChanged}),
				Arguments.of(1, new Dataset[]{STATE[0], STATE[1], first, second}, new Dataset[]{first, second}),
				Arguments.of(1, new Dataset[]{STATE[0], STATE[1], first, firstChanged, second}, new Dataset[]{firstChanged, second}),
				Arguments.of(1, new Dataset[]{STATE[0], STATE[1], first, firstChanged}, new Dataset[]{firstChanged}),
				Arguments.of(2, new Dataset[0], new Dataset[0]),
				Arguments.of(2, new Dataset[]{STATE[0]}, new Dataset[0]),
				Arguments.of(2, new Dataset[]{first, firstChanged}, new Dataset[0]),
				Arguments.of(2, new Dataset[]{STATE[0], STATE[1], first, second, third}, new Dataset[]{third}),
				Arguments.of(2, new Dataset[]{STATE[0], STATE[1], first, second, third, thirdChanged}, new Dataset[]{thirdChanged}),
				Arguments.of(2, new Dataset[]{STATE[0], STATE[1], first, second, third, fourth}, new Dataset[]{third, fourth}),
				Arguments.of(2, new Dataset[]{STATE[0], STATE[1], first, second, third, thirdChanged, fourth}, new Dataset[]{thirdChanged, fourth}),
				Arguments.of(-1, new Dataset[0], new Dataset[0]));
	}

	private static Stream<Arguments> find_params() {
		Dataset.PrimaryKey key = new Dataset.PrimaryKey(PROJECT1.getPrimaryKey(), UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7"));
		Entity<Schema.PrimaryKey> s1 = new Entity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated());
		Dataset first = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 1, false, "name", s1),
				second = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 2, true, "name", s1);
		return Stream.of(Arguments.of(key, 1, new Dataset[0], null),
				Arguments.of(key, 1, new Dataset[]{first}, first),
				Arguments.of(key, 2, new Dataset[]{first, second}, second),
				Arguments.of(key, -3, new Dataset[0], null),
				Arguments.of(key, 10, new Dataset[]{first}, null),
				Arguments.of(key, -10, new Dataset[]{first, second}, null),
				Arguments.of(null, 1, new Dataset[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Dataset.PrimaryKey key = new Dataset.PrimaryKey(PROJECT1.getPrimaryKey(), UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7"));
		Entity<Schema.PrimaryKey> s1 = new Entity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated());
		Dataset first = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 1, false, "name", s1),
				second = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 2, true, "name", s1);
		return Stream.of(Arguments.of(key, new Dataset[0], false),
				Arguments.of(key, new Dataset[]{first}, true),
				Arguments.of(key, new Dataset[]{second}, false),
				Arguments.of(null, new Dataset[0], false));
	}

	private static Stream<Arguments> save_params() {
		UUID key = UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7");
		Entity<Schema.PrimaryKey> s1 = new Entity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated()),
				s2 = new Entity<>(SCHEMA2.getPrimaryKey(), SCHEMA2.getVersion(), SCHEMA2.isDeprecated());
		Dataset first = new Dataset(PROJECT1.getPrimaryKey(), key, 1, false, "name", s1),
				second = new Dataset(PROJECT1.getPrimaryKey(), key, 2, false, "name", s2);
		return Stream.of(Arguments.of(first, new Dataset[0], new Dataset[]{STATE[0], STATE[1], first}, true, 2, 3),
				Arguments.of(second, new Dataset[]{first}, new Dataset[]{STATE[0], STATE[1], first, second}, true, 1, 2),
				Arguments.of(null, new Dataset[0], new Dataset[]{STATE[0], STATE[1]}, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Dataset.PrimaryKey key = new Dataset.PrimaryKey(PROJECT1.getPrimaryKey(), UUID.fromString("4f809af7-2c99-43f7-b674-4843c77384c7"));
		Entity<Schema.PrimaryKey> s1 = new Entity<>(SCHEMA1.getPrimaryKey(), SCHEMA1.getVersion(), SCHEMA1.isDeprecated());
		Dataset before = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 1, false, "name", s1),
				after = new Dataset(PROJECT1.getPrimaryKey(), key.getId(), 1, true, "name", s1);
		return Stream.of(Arguments.of(key, new Dataset[0], new Dataset[]{STATE[0], STATE[1]}, false),
				Arguments.of(key, new Dataset[]{before}, new Dataset[]{STATE[0], STATE[1], after}, true),
				Arguments.of(null, new Dataset[0], new Dataset[]{STATE[0], STATE[1]}, false));
	}

	private void store(Dataset[] datasets) {
		for (Dataset dataset : datasets) {
			String statement = "MATCH (p:Project {id: $})\n" +
					"MERGE (p)-[:CONTAINS]->(d:Dataset {id : $}) SET d.deprecated = $ WITH d\n" +
					"OPTIONAL MATCH (d)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH d, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (d)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(dd:DatasetDetails {description: $}) WITH dd\n" +
					"MATCH (s:Schema {id: $})-[r:CONTAINS_DETAILS]->(sd:SchemaDetails)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $})\n" +
					"WHERE NOT EXISTS(r.to)\n" +
					"WITH dd, s, r, collect(l) as loci\n" +
					"CREATE (dd)-[:HAS {version: r.version}]->(s)";
			Schema.PrimaryKey schemaKey = dataset.getSchema().getPrimaryKey();
			Query query = new Query(statement, dataset.getPrimaryKey().getProjectId(), dataset.getPrimaryKey().getId(), dataset.isDeprecated(), dataset.getDescription(), schemaKey.getId(), schemaKey.getTaxonId());
			execute(query);
		}
	}

	private Dataset parse(Map<String, Object> row) {
		Entity<Schema.PrimaryKey> schema = new Entity<>(new Schema.PrimaryKey((String) row.get("taxonId"),
				(String) row.get("schemaId")),
				(long) row.get("schemaVersion"),
				(boolean) row.get("schemaDeprecated"));
		return new Dataset(UUID.fromString(row.get("projectId").toString()),
				UUID.fromString(row.get("datasetId").toString()),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("description"),
				schema);
	}

	private Dataset[] findAll() {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)\n" +
				"WHERE r2.version = h.version\n" +
				"MATCH (sd)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)\n" +
				"WITH p, d, r1, dd, h, s, t, collect(l) as loci\n" +
				"RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r1.version as version,\n" +
				"dd.description as description, t.id as taxonId, s.id as schemaId, h.version as schemaVersion, s.deprecated as schemaDeprecated\n" +
				"ORDER BY p.id, d.id, r1.version";
		Result result = query(new Query(statement, PROJECT1.getPrimaryKey()));
		if (result == null) return new Dataset[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Dataset[]::new);
	}

	private boolean hasDescendents(Dataset.PrimaryKey key) {
		String statement = "MATCH (p:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
				"OPTIONAL MATCH (d)-[:CONTAINS]->(pf:Profile) WHERE pf.deprecated = false WITH d, pf\n" +
				"OPTIONAL MATCH (d)-[:CONTAINS]->(i:Isolate) WHERE i.deprecated = false = false WITH d, pf, i\n" +
				"RETURN (COUNT(pf) + COUNT(i)) <> 0";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getId()));
	}

	@BeforeEach
	public void init() {
		userRepository.save(USER1);
		projectRepository.save(PROJECT1);
		taxonRepository.save(TAXON1);
		locusRepository.save(LOCUS1);
		locusRepository.save(LOCUS2);
		schemaRepository.save(SCHEMA1);
		schemaRepository.save(SCHEMA2);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Dataset[] state, Dataset[] expected) {
		store(state);
		Optional<List<Dataset>> result = datasetRepository.findAll(page, LIMIT, PROJECT1.getPrimaryKey());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Dataset> projects = result.get();
		assertEquals(expected.length, projects.size());
		assertArrayEquals(expected, projects.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Dataset.PrimaryKey key, long version, Dataset[] state, Dataset expected) {
		store(DatasetRepositoryTests.STATE);
		store(state);
		Optional<Dataset> result = datasetRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Dataset.PrimaryKey key, Dataset[] state, boolean expected) {
		store(DatasetRepositoryTests.STATE);
		store(state);
		boolean result = datasetRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Dataset dataset, Dataset[] state, Dataset[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(DatasetRepositoryTests.STATE);
		store(state);
		Optional<QueryStatistics> result = datasetRepository.save(dataset);
		if (executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Dataset[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Dataset.PrimaryKey key, Dataset[] state, Dataset[] expectedState, boolean expectedResult) {
		store(DatasetRepositoryTests.STATE);
		store(state);
		boolean result = datasetRepository.remove(key);
		Dataset[] stateResult = findAll();
		assertEquals(expectedResult, result);
		if (key != null)
			assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

}
