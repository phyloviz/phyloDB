package pt.ist.meic.phylodb.typing.schema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ist.meic.phylodb.RepositoryTests;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class SchemaRepositoryTests extends RepositoryTests {

	@Autowired
	private TaxonRepository taxonRepository;
	@Autowired
	private LocusRepository locusRepository;
	@Autowired
	private SchemaRepository schemaRepository;

	private static final int LIMIT = 2;
	private static final Taxon taxon = new Taxon("t", null);
	private static final Locus locus1 = new Locus(taxon.getPrimaryKey(), "1", 1, false, "description");
	private static final Locus locus2 = new Locus(taxon.getPrimaryKey(), "2", 1, false, null);
	private static final Schema schema1 = new Schema(taxon.getPrimaryKey(), "1one", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()), new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())));
	private static final Schema schema2 = new Schema(taxon.getPrimaryKey(), "2two", 1, false, Method.MLST, null,
			Arrays.asList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()), new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated())));
	private static final Schema[] state = new Schema[]{schema1, schema2};

	private void store(Schema[] schemas) {
		for (Schema schema : schemas) {
			if (isPresent(schema.getPrimaryKey())) {
				put(schema);
				continue;
			}
			post(schema);
		}
	}

	private void storeWithDataset(Dataset.PrimaryKey key, Schema[] schemas) {
		for (Schema schema : schemas) {
			if (isPresent(schema.getPrimaryKey())) {
				put(schema);
				continue;
			}
			String statement = "CREATE (:Project {id: $})-[:CONTAINS]->(:Dataset {id: $})-[:CONTAINS_DETAILS]->(dd:DatasetDetails)-[:HAS {version: 1}]->(s:Schema {id: $, type: $, deprecated: $})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(sd:SchemaDetails {description: $}) WITH sd\n" +
					"MATCH (t:Taxon {id: $}) WHERE t.deprecated = false WITH t, sd\n";
			Query query = new Query(statement, key.getProjectId(), key.getId(), schema.getPrimaryKey().getId(), schema.getType().getName(), schema.isDeprecated(), schema.getDescription(), schema.getPrimaryKey().getTaxonId());
			composeLoci(schema, query);
			execute(query);
		}

	}

	private boolean isPresent(Schema.PrimaryKey key) {
		String statement = "OPTIONAL MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WITH s, collect(l) as loci\n" +
				"RETURN COALESCE(s.deprecated = false, false)";
		return query(Boolean.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	private void post(Schema schema) {
		String statement = "CREATE (s:Schema {id: $, type: $, deprecated: $})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(sd:SchemaDetails {description: $}) WITH sd\n " +
				"MATCH (t:Taxon {id: $}) WHERE t.deprecated = false WITH t, sd\n";
		Query query = new Query(statement, schema.getPrimaryKey().getId(), schema.getType().getName(), schema.isDeprecated(), schema.getDescription(), schema.getPrimaryKey().getTaxonId());
		composeLoci(schema, query);
		execute(query);
	}

	private void put(Schema schema) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $})\n" +
				"WHERE NOT EXISTS(r.to)\n" +
				"WITH t, s, r, sd, collect(l.id) as loci\n" +
				"SET s.deprecated = $, r.to = datetime() WITH t, s, r.version + 1 as v\n" +
				"CREATE (s)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(sd:SchemaDetails {description: $})\n" +
				"WITH t, sd\n";
		Query query = new Query(statement, schema.getPrimaryKey().getTaxonId(), schema.getPrimaryKey().getId(), schema.isDeprecated(), schema.getDescription());
		composeLoci(schema, query);
		execute(query);
	}

	private void composeLoci(Schema schema, Query query) {
		List<String> ids = schema.getLociIds();
		for (int i = 0; i < ids.size(); i++) {
			query.appendQuery("MATCH (t)-[:CONTAINS]->(l%s:Locus {id: $})-[r:CONTAINS_DETAILS]->(:LocusDetails)\n" +
					"WHERE l%s.deprecated = false AND NOT EXISTS(r.to)\n" +
					"CREATE (sd)-[:HAS {part: %s, version: r.version}]->(l%s) WITH sd, t\n", i, i, i + 1, i)
					.addParameter(ids.get(i));
		}
		query.subQuery(query.length() - "WITH sd, t\n".length());
	}

	private Schema parse(Map<String, Object> row) {
		String taxonId = (String) row.get("taxonId");
		List<Entity<Locus.PrimaryKey>> lociIds = Arrays.stream((Map<String, Object>[]) row.get("lociIds"))
				.map(m -> new Entity<>(new Locus.PrimaryKey(taxonId, (String) m.get("id")) , (long) m.get("version"), (boolean) m.get("deprecated")))
				.collect(Collectors.toList());
		return new Schema(taxonId,
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				Method.valueOf(((String) row.get("type")).toUpperCase()),
				(String) row.get("description"),
				lociIds);
	}

	private Schema[] findAll() {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema)\n" +
				"WITH t, s, r, sd, h, l\n" +
				"ORDER BY h.part\n" +
				"WITH t, s, r, sd, collect({id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds\n" +
				"RETURN t.id as taxonId, s.id as id, s.type as type, s.deprecated as deprecated, r.version as version, " +
				"sd.description as description, lociIds\n" +
				"ORDER BY t.id, s.id, version";
		Result result = query(new Query(statement, taxon.getPrimaryKey()));
		if (result == null) return new Schema[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Schema[]::new);
	}

	private static Stream<Arguments> findAll_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = taxon.getPrimaryKey();
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci2 = Arrays.asList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()),
				new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()));
		Schema first = new Schema(taxonKey, id1, 1, false, Method.MLST, "description", loci1),
				firstChanged = new Schema(taxonKey, id1, 2, false, Method.MLST, "description2", loci1),
				second = new Schema(taxonKey, "4test", 1, false, Method.MLST, null, loci2),
				third = new Schema(taxonKey, id3 , 1, false, Method.MLST, "description3", loci2),
				thirdChanged = new Schema(taxonKey, id3, 2, false, Method.MLST, null, loci1),
				fourth = new Schema(taxonKey, "6test", 1, false, Method.MLST, null, loci2);
		return Stream.of(Arguments.of(0, new Schema[0], new Schema[0]),
				Arguments.of(0, new Schema[]{state[0]}, new Schema[]{state[0]}),
				Arguments.of(0, new Schema[]{first, firstChanged}, new Schema[]{firstChanged}),
				Arguments.of(0, new Schema[]{state[0], state[1], first}, state),
				Arguments.of(0, new Schema[]{state[0], state[1], first, firstChanged}, state),
				Arguments.of(1, new Schema[0], new Schema[0]),
				Arguments.of(1, new Schema[]{state[0]}, new Schema[0]),
				Arguments.of(1, new Schema[]{first, firstChanged}, new Schema[0]),
				Arguments.of(1, new Schema[]{state[0], state[1], first}, new Schema[]{first}),
				Arguments.of(1, new Schema[]{state[0], state[1], first, firstChanged}, new Schema[]{firstChanged}),
				Arguments.of(1, new Schema[]{state[0], state[1], first, second}, new Schema[]{first, second}),
				Arguments.of(1, new Schema[]{state[0], state[1], first, firstChanged, second}, new Schema[]{firstChanged, second}),
				Arguments.of(1, new Schema[]{state[0], state[1], first, firstChanged}, new Schema[]{firstChanged}),
				Arguments.of(2, new Schema[0], new Schema[0]),
				Arguments.of(2, new Schema[]{state[0]}, new Schema[0]),
				Arguments.of(2, new Schema[]{first, firstChanged}, new Schema[0]),
				Arguments.of(2, new Schema[]{state[0], state[1], first, second, third}, new Schema[]{third}),
				Arguments.of(2, new Schema[]{state[0], state[1], first, second, third, thirdChanged}, new Schema[]{thirdChanged}),
				Arguments.of(2, new Schema[]{state[0], state[1], first, second, third, fourth}, new Schema[]{third, fourth}),
				Arguments.of(2, new Schema[]{state[0], state[1], first, second, third, thirdChanged, fourth}, new Schema[]{thirdChanged, fourth}),
				Arguments.of(-1, new Schema[0], new Schema[0]));
	}

	private static Stream<Arguments> find_params() {
		Schema.PrimaryKey key = new Schema.PrimaryKey(taxon.getPrimaryKey(), "test");
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci1Changed = Arrays.asList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()),
				new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci2 = Collections.singletonList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci2Changed = Collections.singletonList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		Schema schema1 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.MLST, "description", loci1),
				schema1Changed = new Schema(key.getTaxonId(), key.getId(), 2, false, Method.MLST, "description2", loci1Changed),
				schema2 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.SNP, null, loci2),
				schema2Changed = new Schema(key.getTaxonId(), key.getId(), 2, false, Method.SNP, "descriptionChanged", loci2Changed);
		return Stream.of(Arguments.of(key, 1, new Schema[0], null),
				Arguments.of(key, 1, new Schema[]{schema1}, schema1),
				Arguments.of(key, 2, new Schema[]{schema1, schema1Changed}, schema1Changed),
				Arguments.of(key, 1, new Schema[]{schema2}, schema2),
				Arguments.of(key, 2, new Schema[]{schema2, schema2Changed}, schema2Changed),
				Arguments.of(key, -3, new Schema[0], null),
				Arguments.of(key, 10, new Schema[]{schema1}, null),
				Arguments.of(key, -10, new Schema[]{schema1, schema1Changed}, null),
				Arguments.of(key, 3, new Schema[]{schema2}, null),
				Arguments.of(key, -11, new Schema[]{schema2, schema2Changed}, null),
				Arguments.of(null, 1, new Schema[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Schema.PrimaryKey key = new Schema.PrimaryKey(taxon.getPrimaryKey(), "test");
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci2 = Collections.singletonList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()));
		Schema schema1 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.MLST, "description", loci1),
				schema1Deleted = new Schema(key.getTaxonId(), key.getId(), 1, true, Method.MLST, "description", loci1),
				schema2 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.SNP, null, loci2),
				schema2Deleted = new Schema(key.getTaxonId(), key.getId(), 1, true, Method.SNP, null, loci2);
		return Stream.of(Arguments.of(key, new Schema[0], false),
				Arguments.of(key, new Schema[]{schema1}, true),
				Arguments.of(key, new Schema[]{schema1Deleted}, false),
				Arguments.of(key, new Schema[]{schema2}, true),
				Arguments.of(key, new Schema[]{schema2Deleted}, false),
				Arguments.of(null, new Schema[0], false));
	}

	private static Stream<Arguments> save_params() {
		Schema.PrimaryKey key = new Schema.PrimaryKey(taxon.getPrimaryKey(), "3three");
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci2 = Arrays.asList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()),
				new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()));
		List<Entity<Locus.PrimaryKey>> loci3 = Collections.singletonList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		Schema schema1 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.MLST, "description", loci1),
				schema1Changed = new Schema(key.getTaxonId(), key.getId(), 2, false, Method.MLST, "description2", loci2),
				schema2 = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.SNP, "description3", loci3),
				schema2Changed = new Schema(key.getTaxonId(), key.getId(), 2, false, Method.SNP, "description4", loci1);
		return Stream.of(Arguments.of(schema1, new Schema[0], new Schema[]{state[0], state[1], schema1}, true, 2, 3),
				Arguments.of(schema2, new Schema[0], new Schema[]{state[0], state[1], schema2}, true, 2, 2),
				Arguments.of(schema1Changed, new Schema[]{schema1}, new Schema[]{state[0], state[1], schema1, schema1Changed}, true, 1, 3),
				Arguments.of(schema2Changed, new Schema[]{schema2}, new Schema[]{state[0], state[1], schema2, schema2Changed}, true, 1, 3),
				Arguments.of(null, new Schema[0], state, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Schema.PrimaryKey key = new Schema.PrimaryKey(taxon.getPrimaryKey(), "3three");
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		Schema first = new Schema(key.getTaxonId(), key.getId(), 1, false, Method.MLST, "description", loci1),
				second = new Schema(key.getTaxonId(), key.getId(), 1, true, Method.MLST, "description", loci1);
		return Stream.of(Arguments.of(key, new Schema[0], state, false),
				Arguments.of(key, new Schema[]{first}, new Schema[]{state[0], state[1], second}, true),
				Arguments.of(null, new Schema[0], state, false));
	}

	private static Stream<Arguments> findByLoci_params() {
		String[] existentLoci = state[0].getLociIds().toArray(new String[0]),
				loci1NotExists = new String[] {"not", "exists"};
		Schema schema = new Schema(taxon.getPrimaryKey(), "3three", 1, false, Method.SNP, null,
				Collections.singletonList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())));
		return Stream.of(Arguments.of(taxon.getPrimaryKey(), state[0].getType(), existentLoci, state, state[0]),
				Arguments.of(taxon.getPrimaryKey(), schema.getType(), new String[] {locus2.getPrimaryKey().getId()}, new Schema[] {state[0], state[1], schema}, schema),
				Arguments.of(taxon.getPrimaryKey(), state[0].getType(), loci1NotExists, state, null),
				Arguments.of(taxon.getPrimaryKey(), state[0].getType(), null, state, null),
				Arguments.of(taxon.getPrimaryKey(), state[0].getType(), new String[0], state, null),
				Arguments.of(null, state[0].getType(), existentLoci, state, null));
	}

	private static Stream<Arguments> findByDataset_params() {
		Dataset.PrimaryKey key = new Dataset.PrimaryKey(UUID.randomUUID(), UUID.randomUUID());
		List<Entity<Locus.PrimaryKey>> loci1 = Arrays.asList(new Entity<>(locus1.getPrimaryKey(), locus1.getVersion(), locus1.isDeprecated()),
				new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated())),
				loci2 = Collections.singletonList(new Entity<>(locus2.getPrimaryKey(), locus2.getVersion(), locus2.isDeprecated()));
		Schema schema1 = new Schema(taxon.getPrimaryKey(), "3three", 1, false, Method.SNP, "test", loci1),
				schema1Changed = new Schema(taxon.getPrimaryKey(), "3three", 2, false, Method.SNP, "changed", loci2),
				schema2 = new Schema(taxon.getPrimaryKey(), "4fourth", 1, false, Method.MLVA, null, loci2),
				schema2Changed = new Schema(taxon.getPrimaryKey(), "4fourth", 2, false, Method.MLVA, "description", loci1);
		return Stream.of(Arguments.of(key, key, new Schema[] {schema1}, schema1),
				Arguments.of(key, key, new Schema[] {schema1, schema1Changed}, schema1),
				Arguments.of(key, key, new Schema[] {schema2}, schema2),
				Arguments.of(key, key, new Schema[] {schema2, schema2Changed}, schema2),
				Arguments.of(new Dataset.PrimaryKey(UUID.randomUUID(), UUID.randomUUID()), key, new Schema[] {schema1}, null),
				Arguments.of(null, key, new Schema[] {schema1}, null));
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(taxon);
		locusRepository.save(locus1);
		locusRepository.save(locus2);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Schema[] state, Schema[] expected) {
		store(state);
		Optional<List<Schema>> result = schemaRepository.findAll(page, LIMIT, taxon.getPrimaryKey());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Schema> schemas = result.get();
		assertEquals(expected.length,  schemas.size());
		assertArrayEquals(expected, schemas.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Schema.PrimaryKey key, long version, Schema[] state, Schema expected) {
		store(SchemaRepositoryTests.state);
		store(state);
		Optional<Schema> result = schemaRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Schema.PrimaryKey key, Schema[] state, boolean expected) {
		store(SchemaRepositoryTests.state);
		store(state);
		boolean result = schemaRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Schema schema, Schema[] state, Schema[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(SchemaRepositoryTests.state);
		store(state);
		Optional<QueryStatistics> result = schemaRepository.save(schema);
		if(executed) {
			assertTrue(result.isPresent());
			assertEquals(nodesCreated, result.get().getNodesCreated());
			assertEquals(relationshipsCreated, result.get().getRelationshipsCreated());
		} else
			assertFalse(result.isPresent());
		Schema[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Schema.PrimaryKey key, Schema[] state, Schema[] expectedState, boolean expectedResult) {
		store(SchemaRepositoryTests.state);
		store(state);
		boolean result = schemaRepository.remove(key);
		Schema[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("findByLoci_params")
	public void findByLoci(String taxonId, Method type, String[] lociIds, Schema[] state, Schema expected) {
		store(state);
		Optional<Schema> result = schemaRepository.find(taxonId, type, lociIds);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("findByDataset_params")
	public void findByDataset(Dataset.PrimaryKey key, Dataset.PrimaryKey datasetState, Schema[] state, Schema expected) {
		store(SchemaRepositoryTests.state);
		storeWithDataset(datasetState, state);
		Optional<Schema> result = schemaRepository.find(key);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

}
