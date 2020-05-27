package pt.ist.meic.phylodb.analysis.inference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class InferenceRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Inference[] STATE = new Inference[]{INFERENCE1, INFERENCE2};

	private static Stream<Arguments> findAll_params() {
		String key1 = "6f809af7-2c99-43f7-b674-4843c77384c7", key2 = "7f809af7-2c99-43f7-b674-4843c77384c7";
		Edge edge3 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 3);
		Edge edge4 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), 4);
		Inference first = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key1, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge3, edge4)),
				second = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "6f909af7-2c99-43f7-b674-4843c77384c7", false, InferenceAlgorithm.GOEBURST, Arrays.asList(EDGES1, EDGES2)),
				third = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key2, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge3, edge4)),
				fourth = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), "8f809af7-2c99-43f7-b674-4843c77384c7", false, InferenceAlgorithm.GOEBURST, Arrays.asList(EDGES1, EDGES2));
		return Stream.of(Arguments.of(0, new Inference[0], new Inference[0]),
				Arguments.of(0, new Inference[]{STATE[0]}, new Inference[]{STATE[0]}),
				Arguments.of(0, new Inference[]{STATE[0], STATE[1], first}, STATE),
				Arguments.of(1, new Inference[0], new Inference[0]),
				Arguments.of(1, new Inference[]{STATE[0]}, new Inference[0]),
				Arguments.of(1, new Inference[]{STATE[0], STATE[1], first}, new Inference[]{first}),
				Arguments.of(1, new Inference[]{STATE[0], STATE[1], first, second}, new Inference[]{first, second}),
				Arguments.of(2, new Inference[0], new Inference[0]),
				Arguments.of(2, new Inference[]{STATE[0]}, new Inference[0]),
				Arguments.of(2, new Inference[]{STATE[0], STATE[1], first, second, third}, new Inference[]{third}),
				Arguments.of(2, new Inference[]{STATE[0], STATE[1], first, second, third, fourth}, new Inference[]{third, fourth}),
				Arguments.of(-1, new Inference[0], new Inference[0]));
	}

	private static Stream<Arguments> find_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Edge edge3 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 3);
		Edge edge4 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), 4);
		Inference first = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge3, edge4));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Inference[0], null),
				Arguments.of(first.getPrimaryKey(), new Inference[]{first}, first),
				Arguments.of(null, new Inference[0], null));
	}

	private static Stream<Arguments> exists_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Edge edge3 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 3);
		Edge edge4 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), 4);
		Inference first = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge4, edge3)),
				second = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, true, InferenceAlgorithm.GOEBURST, Arrays.asList(edge4, edge3));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Inference[0], false),
				Arguments.of(first.getPrimaryKey(), new Inference[]{first}, true),
				Arguments.of(second.getPrimaryKey(), new Inference[]{second}, false),
				Arguments.of(null, new Inference[0], false));
	}

	private static Stream<Arguments> save_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Edge edge3 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 3);
		Edge edge4 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), 2);
		Inference first = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge4, edge3));
		return Stream.of(Arguments.of(first, new Inference[0], new Inference[]{STATE[0], STATE[1], first}, true, 0, 2),
				Arguments.of(null, new Inference[0], new Inference[]{STATE[0], STATE[1]}, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Edge edge3 = new Edge(new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), new VersionedEntity<>(PROFILE3.getPrimaryKey(), PROFILE3.getVersion(), PROFILE3.isDeprecated()), 3);
		Edge edge4 = new Edge(new VersionedEntity<>(PROFILE2.getPrimaryKey(), PROFILE2.getVersion(), PROFILE2.isDeprecated()), new VersionedEntity<>(PROFILE1.getPrimaryKey(), PROFILE1.getVersion(), PROFILE1.isDeprecated()), 2);
		Inference first = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, false, InferenceAlgorithm.GOEBURST, Arrays.asList(edge4, edge3)),
				second = new Inference(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), key, true, InferenceAlgorithm.GOEBURST, Arrays.asList(edge4, edge3));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Inference[0], new Inference[]{STATE[0], STATE[1]}, false),
				Arguments.of(second.getPrimaryKey(), new Inference[]{first}, new Inference[]{STATE[0], STATE[1], second}, true),
				Arguments.of(null, new Inference[0], new Inference[]{STATE[0], STATE[1]}, false));
	}

	private void store(Inference[] inferences) {
		for (Inference inference : inferences) {
			String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
					"WHERE d.deprecated = false\n" +
					"WITH d, $ as treeId, $ as algorithm, $ as deprecated\n" +
					"UNWIND $ as edge\n" +
					"MATCH (d)-[:CONTAINS]->(p1:Profile {id: edge.from})-[r1:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
					"WHERE NOT EXISTS(r1.to)\n" +
					"MATCH (d)-[:CONTAINS]->(p2:Profile {id: edge.to})-[r2:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
					"WHERE NOT EXISTS(r2.to)\n" +
					"CREATE (p1)-[:DISTANCES {id: treeId, deprecated: deprecated, algorithm: algorithm, fromVersion: r1.version, toVersion: r2.version, distance: edge.distance}]->(p2)";
			Inference.PrimaryKey key = inference.getPrimaryKey();
			Query query = new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId(), inference.getAlgorithm().getName(), inference.isDeprecated(),
					inference.getEdges().stream()
							.map(e -> new Object() {
								public final String from = e.getFrom().getPrimaryKey().getId();
								public final String to = e.getTo().getPrimaryKey().getId();
								public final int distance = e.getWeight();
							})
			);
			execute(query);
		}
	}

	private Inference parse(Map<String, Object> row) {
		List<Edge> list = new ArrayList<>();
		String projectId = (String) row.get("projectId");
		String datasetId = (String) row.get("datasetId");
		for (Map<String, Object> edge: (Map<String, Object>[]) row.get("edges")) {
			VersionedEntity<Profile.PrimaryKey> from = new VersionedEntity<>(new Profile.PrimaryKey(projectId, datasetId, (String) edge.get("from")), (long) edge.get("fromVersion"), (boolean) edge.get("fromDeprecated"));
			VersionedEntity<Profile.PrimaryKey> to = new VersionedEntity<>(new Profile.PrimaryKey(projectId, datasetId, (String) edge.get("to")), (long) edge.get("toVersion"), (boolean) edge.get("toDeprecated"));
			list.add(new Edge(from, to, Math.toIntExact((long) edge.get("distance"))));
		}
		return new Inference(projectId,
				datasetId,
				(String) row.get("id"),
				(boolean) row.get("deprecated"), InferenceAlgorithm.valueOf(row.get("algorithm").toString().toUpperCase()),
				list
		);
	}

	private Inference[] findAll() {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES]->(p2:Profile)\n" +
				"WITH pj, ds, p1, p2, d\n" +
				"ORDER BY d.distance\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, d.id as id, d.deprecated as deprecated, d.algorithm as algorithm,\n" +
				"collect(DISTINCT {from: p1.id, fromVersion: d.fromVersion, fromDeprecated: p1.deprecated, distance: d.distance,\n" +
				"to: p2.id, toVersion: d.toVersion, toDeprecated: p2.deprecated}) as edges\n" +
				"ORDER BY pj.id, ds.id, size(d.id), d.id";
		Result result = query(new Query(statement, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId()));
		if (result == null) return new Inference[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Inference[]::new);
	}

	private boolean hasDescendents(Inference.PrimaryKey key) {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p1:Profile)-[d:DISTANCES {id: $}]->(p2:Profile)\n" +
				"WITH ds, d.id as analysis, collect(d) as ignored\n" +
				"OPTIONAL MATCH (ds)-[:CONTAINS]->(p:Profile)-[:HAS]->(c:Coordinate {analysisId: analysis})\n" +
				"WHERE c.deprecate = false\n" +
				"RETURN COUNT(c) <> 0";
		return query(Boolean.class, new Query(statement, key.getProjectId(), key.getDatasetId(), key.getId()));
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
		profileRepository.save(PROFILE3);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Inference[] state, Inference[] expected) {
		store(state);
		Optional<List<Inference>> result = inferenceRepository.findAllEntities(page, LIMIT, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Inference> projects = result.get();
		assertEquals(expected.length, projects.size());
		assertArrayEquals(expected, projects.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Inference.PrimaryKey key, Inference[] state, Inference expected) {
		store(InferenceRepositoryTests.STATE);
		store(state);
		Optional<Inference> result = inferenceRepository.find(key);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Inference.PrimaryKey key, Inference[] state, boolean expected) {
		store(InferenceRepositoryTests.STATE);
		store(state);
		boolean result = inferenceRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Inference inference, Inference[] state, Inference[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(InferenceRepositoryTests.STATE);
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = inferenceRepository.save(inference);
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		Inference[] stateResult = findAll();
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Inference.PrimaryKey key, Inference[] state, Inference[] expectedState, boolean expectedResult) {
		store(InferenceRepositoryTests.STATE);
		store(state);
		boolean result = inferenceRepository.remove(key);
		Inference[] stateResult = findAll();
		assertEquals(expectedResult, result);
		if (key != null)
			assertFalse(hasDescendents(key));
		assertArrayEquals(expectedState, stateResult);
	}

}
