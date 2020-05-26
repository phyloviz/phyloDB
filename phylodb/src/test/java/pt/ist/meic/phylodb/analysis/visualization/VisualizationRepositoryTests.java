package pt.ist.meic.phylodb.analysis.visualization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.analysis.visualization.model.Coordinate;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class VisualizationRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final Visualization[] STATE = new Visualization[]{VISUALIZATION1, VISUALIZATION2};

	private static Stream<Arguments> findAll_params() {
		String key1 = "6f809af7-2c99-43f7-b674-4843c77384c7", key2 = "7f809af7-2c99-43f7-b674-4843c77384c7";
		Coordinate coordinate1 = new Coordinate(PROFILE1.getPrimaryKey(), 44, 44);
		Coordinate coordinate2 = new Coordinate(PROFILE2.getPrimaryKey(), 55, 55);
		Visualization first = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key1, false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(coordinate1, coordinate2)),
				second = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), "6f909af7-2c99-43f7-b674-4843c77384c7", false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13)),
				third = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(),  key2, false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(coordinate1, coordinate2)),
				fourth = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(),  "8f809af7-2c99-43f7-b674-4843c77384c7", false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT,  Arrays.asList(COORDINATE21, COORDINATE22, COORDINATE23));
		return Stream.of(Arguments.of(0, new Visualization[0], new Visualization[0]),
				Arguments.of(0, new Visualization[]{STATE[0]}, new Visualization[]{STATE[0]}),
				Arguments.of(0, new Visualization[]{STATE[0], STATE[1], first}, STATE),
				Arguments.of(1, new Visualization[0], new Visualization[0]),
				Arguments.of(1, new Visualization[]{STATE[0]}, new Visualization[0]),
				Arguments.of(1, new Visualization[]{STATE[0], STATE[1], first}, new Visualization[]{first}),
				Arguments.of(1, new Visualization[]{STATE[0], STATE[1], first, second}, new Visualization[]{first, second}),
				Arguments.of(2, new Visualization[0], new Visualization[0]),
				Arguments.of(2, new Visualization[]{STATE[0]}, new Visualization[0]),
				Arguments.of(2, new Visualization[]{STATE[0], STATE[1], first, second, third}, new Visualization[]{third}),
				Arguments.of(2, new Visualization[]{STATE[0], STATE[1], first, second, third, fourth}, new Visualization[]{third, fourth}),
				Arguments.of(-1, new Visualization[0], new Visualization[0]));
	}

	private static Stream<Arguments> find_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Visualization first = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key, false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Visualization[0], null),
				Arguments.of(first.getPrimaryKey(), new Visualization[]{first}, first),
				Arguments.of(null, new Visualization[0], null));
	}

	private static Stream<Arguments> exists_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Visualization first = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key, false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13)),
				second = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key, true, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Visualization[0], false),
				Arguments.of(first.getPrimaryKey(), new Visualization[]{first}, true),
				Arguments.of(second.getPrimaryKey(), new Visualization[]{second}, false),
				Arguments.of(null, new Visualization[0], false));
	}

	private static Stream<Arguments> remove_params() {
		String key = "6f809af7-2c99-43f7-b674-4843c77384c7";
		Visualization first = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key, false, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13)),
				second = new Visualization(PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId(), key, true, VisualizationAlgorithm.FORCE_DIRECTED_LAYOUT, Arrays.asList(COORDINATE11, COORDINATE12, COORDINATE13));
		return Stream.of(Arguments.of(first.getPrimaryKey(), new Visualization[0], new Visualization[]{STATE[0], STATE[1]}, false),
				Arguments.of(second.getPrimaryKey(), new Visualization[]{first}, new Visualization[]{STATE[0], STATE[1], second}, true),
				Arguments.of(null, new Visualization[0], new Visualization[]{STATE[0], STATE[1]}, false));
	}

	private void store(Visualization[] visualizations) {
		for (Visualization visualization : visualizations) {
			String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(d:Dataset {id: $})\n" +
					"WHERE d.deprecated = false\n" +
					"WITH pj, d, $ as analysis, $ as id, $ as algorithm, $ as deprecated\n" +
					"UNWIND $ as coordinate\n" +
					"MATCH (d)-[:CONTAINS]->(p:Profile {id: coordinate.profile})-[r:CONTAINS_DETAILS]->(:ProfileDetails)\n" +
					"WHERE NOT EXISTS(r.to)\n" +
					"CREATE (p)-[:HAS {analysis: analysis, id: id, algorithm: algorithm, deprecated: deprecated}]->(:Coordinate {x: coordinate.x, y: coordinate.y})";
			Visualization.PrimaryKey key = visualization.getPrimaryKey();
			Query query = new Query(statement, key.getProjectId(), key.getDatasetId(), key.getAnalysisId(), key.getId(), visualization.getAlgorithm().getName(), visualization.isDeprecated(),
					visualization.getCoordinates().stream()
							.map(c -> new Object() {
								public final int x = c.getX();
								public final int y = c.getY();
								public final String profile = c.getProfile().getId();
							})
			);
			execute(query);
		}
	}

	private Visualization parse(Map<String, Object> row) {
		List<Coordinate> list = new ArrayList<>();
		String projectId = row.get("projectId").toString();
		String datasetId = row.get("datasetId").toString();
		for (Map<String, Object> coordinates: (Map<String, Object>[]) row.get("coordinates")) {
			Profile.PrimaryKey profile = new Profile.PrimaryKey(projectId, datasetId, (String) coordinates.get("profileId"));
			list.add(new Coordinate(profile, Math.toIntExact((long) coordinates.get("x")), Math.toIntExact((long) coordinates.get("y"))));
		}
		return new Visualization(projectId,
				datasetId,
				row.get("analysisId").toString(),
				row.get("id").toString(),
				(boolean) row.get("deprecated"),
				VisualizationAlgorithm.valueOf(row.get("algorithm").toString().toUpperCase()),
				list
		);
	}

	private Visualization[] findAll() {
		String statement = "MATCH (pj:Project {id: $})-[:CONTAINS]->(ds:Dataset {id: $})\n" +
				"MATCH (ds)-[:CONTAINS]->(p:Profile)-[h:HAS {analysis: $}]->(c:Coordinate)\n" +
				"WITH pj, ds, h.analysis as analysis, p, h, c\n" +
				"ORDER BY pj.id, ds.id, analysis, size(h.id), h.id, p.id, c.x, c.y\n" +
				"RETURN pj.id as projectId, ds.id as datasetId, analysis as analysisId, h.id as id, h.deprecated as deprecated, h.algorithm as algorithm,\n" +
				"collect(DISTINCT {profileId: p.id, x: c.x, y: c.y}) as coordinates\n" +
				"ORDER BY pj.id, ds.id, analysis, size(h.id), h.id";
		Result result = query(new Query(statement, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId()));
		if (result == null) return new Visualization[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Visualization[]::new);
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
		inferenceRepository.save(INFERENCE1);
		inferenceRepository.save(INFERENCE2);
	}

	@ParameterizedTest
	@MethodSource("findAll_params")
	public void findAll(int page, Visualization[] state, Visualization[] expected) {
		store(state);
		Optional<List<Visualization>> result = visualizationRepository.findAll(page, LIMIT, PROJECT1.getPrimaryKey(), DATASET1.getPrimaryKey().getId(), INFERENCE1.getPrimaryKey().getId());
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Visualization> projects = result.get();
		assertEquals(expected.length, projects.size());
		assertArrayEquals(expected, projects.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Visualization.PrimaryKey key, Visualization[] state, Visualization expected) {
		store(VisualizationRepositoryTests.STATE);
		store(state);
		Optional<Visualization> result = visualizationRepository.find(key);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Visualization.PrimaryKey key, Visualization[] state, boolean expected) {
		store(VisualizationRepositoryTests.STATE);
		store(state);
		boolean result = visualizationRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Visualization.PrimaryKey key, Visualization[] state, Visualization[] expectedState, boolean expectedResult) {
		store(VisualizationRepositoryTests.STATE);
		store(state);
		boolean result = visualizationRepository.remove(key);
		Visualization[] stateResult = findAll();
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

}
