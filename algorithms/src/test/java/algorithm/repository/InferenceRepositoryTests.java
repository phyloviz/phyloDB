package algorithm.repository;

import algorithm.inference.InferenceProcedures;
import algorithm.inference.InferenceRepository;
import algorithm.inference.implementation.GoeBURST;
import algorithm.inference.model.Edge;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;
import algorithm.repository.type.Distance;
import algorithm.repository.type.Profile;
import algorithm.repository.type.Relation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Config;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class InferenceRepositoryTests extends RepositoryTests {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset",
			PROFILE1_ID = "1", PROFILE2_ID = "2", PROFILE3_ID = "3";

	@Rule
	public Neo4jRule neo4j = new Neo4jRule().withProcedure(InferenceProcedures.class);

	public URI uri;
	public Config config;
	public InferenceRepository repository;

	@Before
	public void init() {
		uri = neo4j.boltURI();
		config = Config.build().withoutEncryption().toConfig();
		database = neo4j.getGraphDatabaseService();
		repository = new InferenceRepository(database);
	}

	@Test
	public void read_matrix2Rows1Column() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-2p-1a.cypher");
			Matrix matrix  = repository.read(PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(matrix.getDistances(), new int[][] {{}, {1}});
			assertArrayEquals(matrix.getIsolates(), new int[] {0, 1});
			tx.failure();
		}
	}

	@Test
	public void read_matrix2RowsNColumn() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-2p-na.cypher");
			Matrix matrix  = repository.read(PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(matrix.getDistances(), new int[][] {{}, {2}});
			assertArrayEquals(matrix.getIsolates(), new int[] {0, 0});
			tx.failure();
		}
	}

	@Test
	public void read_matrixNRows1Column() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-np-1a.cypher");
			Matrix matrix  = repository.read(PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(matrix.getDistances(), new int[][] {{}, {0}, {1, 1}});
			assertArrayEquals(matrix.getIsolates(), new int[] {0, 0, 1});
			tx.failure();
		}
	}

	@Test
	public void read_matrixNRowsNColumn() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-np-na.cypher");
			Matrix matrix  = repository.read(PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(matrix.getDistances(), new int[][] {{}, {2}, {3, 2}});
			assertArrayEquals(matrix.getIsolates(), new int[] {0, 0, 0});
			tx.failure();
		}
	}


	@Test
	public void read_matrixNRowsNColumnWithMissing() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-np-na-m.cypher");
			Matrix matrix  = repository.read(PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(matrix.getDistances(), new int[][] {{}, {2}, {3, 3}});
			assertArrayEquals(matrix.getIsolates(), new int[] {0, 0, 0});
			tx.failure();
		}
	}

	@Test
	public void write_inferenceWith1Edge() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-2p-na.cypher");
			String id = "teste";
			String[] profilesIds = new String[] {PROFILE1_ID, PROFILE2_ID};
			Inference inference = new Inference(PROJECT_ID, DATASET_ID, id, profilesIds, GoeBURST.NAME);
			inference.add(new Edge(0, 1, 2));
			long relationshipsCount = getRelationshipsCount();
			long nodesCount = getNodesCount();
			repository.write(inference);
			assertEquals(relationshipsCount + 1, getRelationshipsCount());
			assertEquals(nodesCount, getNodesCount());
			List<Relationship> edges = database.getAllRelationships().stream()
					.filter(r -> r.getType().equals(RelationshipType.withName(Relation.DISTANCES.name())))
					.collect(Collectors.toList());
			assertEdges(edges, inference.getEdges(), inference.getProfileIds(), id);
			tx.failure();
		}
	}

	@Test
	public void write_inferenceWithNEdge() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange("inference", "ctx-np-na-m.cypher");
			String id = "teste";
			String[] profilesIds = new String[] {PROFILE1_ID, PROFILE2_ID, PROFILE3_ID};
			Inference inference = new Inference(PROJECT_ID, DATASET_ID, id, profilesIds, GoeBURST.NAME);
			inference.add(new Edge(0, 1, 2));
			inference.add(new Edge(1, 2, 3));
			long relationshipsCount = getRelationshipsCount();
			long nodesCount = getNodesCount();
			repository.write(inference);
			assertEquals(relationshipsCount + 2, getRelationshipsCount());
			assertEquals(nodesCount, getNodesCount());
			List<Relationship> edges = database.getAllRelationships().stream()
					.filter(r -> r.getType().equals(RelationshipType.withName(Relation.DISTANCES.name())))
					.collect(Collectors.toList());
			assertEdges(edges, inference.getEdges(), inference.getProfileIds(), id);
			tx.failure();
		}
	}

	private void assertEdges(List<Relationship> result, List<Edge> expected, String[] profilesIds, String inferenceId) {
		assertEquals(expected.size(), result.size());
		for (int i = 0; i < expected.size(); i++) {
			Relationship distances = result.get(i);
			assertEquals(distances.getStartNode().getProperty(Profile.ID), profilesIds[expected.get(i).from()]);
			assertEquals(distances.getEndNode().getProperty(Profile.ID), profilesIds[expected.get(i).to()]);
			assertEquals(distances.getProperty(Distance.ID), inferenceId);
			assertEquals(distances.getProperty(Distance.DISTANCE), expected.get(i).distance());
			assertEquals(distances.getProperty(Distance.FROM_VERSION), 1);
			assertEquals(distances.getProperty(Distance.TO_VERSION), 1);
			assertEquals(distances.getProperty(Distance.ALGORITHM), GoeBURST.NAME);
			assertEquals(distances.getProperty(Distance.DEPRECATED), false);

		}
	}

}
