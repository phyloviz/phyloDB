package algorithm.inference;

import algorithm.inference.implementation.GoeBURST;
import algorithm.inference.model.Edge;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;
import algorithm.repository.RepositoryTests;
import algorithm.utils.type.Distance;
import algorithm.utils.type.Profile;
import algorithm.utils.type.Relation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.Config;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.rule.Neo4jRule;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
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
		config = Config.builder().withoutEncryption().build();
		database = neo4j.defaultDatabaseService();
		repository = new InferenceRepository(database);
	}

	@Test
	public void read_matrix2Rows1Column() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-2p-1a.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(new int[][] {{}, {1}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 1}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void read_matrix2Rows1ColumnWithDeprecated() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-2p-1a-d.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(new int[][] {{}, {1}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 1}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void read_matrix2Rows1ColumnWithVersionIsolate() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-2p-1a-vi.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(new int[][] {{}, {1}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 1}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void read_matrix2RowsNColumn() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-2p-na.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID)));
			assertArrayEquals(new int[][] {{}, {2}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 0}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void read_matrixNRows1Column() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-np-1a.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(new int[][] {{}, {0}, {1, 1}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 0, 1}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void read_matrixNRowsNColumn() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-np-na.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(new int[][] {{}, {2}, {3, 2}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 0, 0}, matrix.getIsolates());
			tx.rollback();
		}
	}


	@Test
	public void read_matrixNRowsNColumnWithMissing() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-np-na-m.cypher");
			Matrix matrix  = repository.read(tx, PROJECT_ID, DATASET_ID);
			assertTrue(Arrays.asList(matrix.getIds()).containsAll(Arrays.asList(PROFILE1_ID, PROFILE2_ID, PROFILE3_ID)));
			assertArrayEquals(new int[][] {{}, {2}, {3, 2}}, matrix.getDistances());
			assertArrayEquals(new int[] {0, 0, 0}, matrix.getIsolates());
			tx.rollback();
		}
	}

	@Test
	public void write_inferenceWith1Edge() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-2p-na.cypher");
			String id = "teste";
			String[] profilesIds = new String[] {PROFILE1_ID, PROFILE2_ID};
			Inference inference = new Inference(PROJECT_ID, DATASET_ID, id, profilesIds, GoeBURST.NAME);
			inference.add(new Edge(0, 1, 2));
			long relationshipsCount = getRelationshipsCount(tx);
			long nodesCount = getNodesCount(tx);
			repository.write(tx, inference);
			assertEquals(relationshipsCount + 1, getRelationshipsCount(tx));
			assertEquals(nodesCount, getNodesCount(tx));
			List<Relationship> edges = tx.getAllRelationships().stream()
					.filter(r -> r.getType().equals(RelationshipType.withName(Relation.DISTANCES.name())))
					.collect(Collectors.toList());
			assertEdges(edges, inference.getEdges(), inference.getProfileIds(), id);
			tx.rollback();
		}
	}

	@Test
	public void write_inferenceWithNEdge() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "inference", "ctx-np-na-m.cypher");
			String id = "teste";
			String[] profilesIds = new String[] {PROFILE1_ID, PROFILE2_ID, PROFILE3_ID};
			Inference inference = new Inference(PROJECT_ID, DATASET_ID, id, profilesIds, GoeBURST.NAME);
			inference.add(new Edge(0, 1, 2));
			inference.add(new Edge(1, 2, 3));
			long relationshipsCount = getRelationshipsCount(tx);
			long nodesCount = getNodesCount(tx);
			repository.write(tx, inference);
			assertEquals(relationshipsCount + 2, getRelationshipsCount(tx));
			assertEquals(nodesCount, getNodesCount(tx));
			List<Relationship> edges = tx.getAllRelationships().stream()
					.filter(r -> r.getType().equals(RelationshipType.withName(Relation.DISTANCES.name())))
					.collect(Collectors.toList());
			assertEdges(edges, inference.getEdges(), inference.getProfileIds(), id);
			tx.rollback();
		}
	}

	private void assertEdges(List<Relationship> result, List<Edge> expected, String[] profilesIds, String inferenceId) {
		assertEquals(expected.size(), result.size());
		for (int i = 0; i < expected.size(); i++) {
			Relationship distances = result.get(i);
			Optional<Edge> edge = expected.stream().filter(e -> profilesIds[e.from()].equals(distances.getStartNode().getProperty(Profile.ID).toString()) &&
					profilesIds[e.to()].equals(distances.getEndNode().getProperty(Profile.ID).toString()))
					.findFirst();
			assertTrue(edge.isPresent());
			Edge e = edge.get();
			assertEquals(distances.getProperty(Distance.ID), inferenceId);
			assertEquals(distances.getProperty(Distance.DISTANCE), e.distance());
			assertEquals(distances.getProperty(Distance.FROM_VERSION), 1);
			assertEquals(distances.getProperty(Distance.TO_VERSION), 1);
			assertEquals(distances.getProperty(Distance.ALGORITHM), GoeBURST.NAME);
			assertEquals(distances.getProperty(Distance.DEPRECATED), false);

		}
	}

}
