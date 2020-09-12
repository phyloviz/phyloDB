package algorithm.visualization;

import algorithm.inference.InferenceProcedures;
import algorithm.repository.RepositoryTests;
import algorithm.utils.type.Has;
import algorithm.utils.type.Profile;
import algorithm.utils.type.Relation;
import algorithm.visualization.implementation.Radial;
import algorithm.visualization.model.Coordinate;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Vertex;
import algorithm.visualization.model.Visualization;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class VisualizationRepositoryTests extends RepositoryTests {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset", INFERENCE_ID = "inference",
			VISUALIZATION_ID = "visualization", PROFILE1_ID = "1", PROFILE2_ID = "2", PROFILE3_ID = "3",
			PROFILE4_ID = "4", PROFILE5_ID = "5", PROFILE6_ID = "6";

	@Rule
	public Neo4jRule neo4j = new Neo4jRule().withProcedure(InferenceProcedures.class);

	public URI uri;
	public Config config;
	public VisualizationRepository repository;


	private static Vertex vertex(String id, int distance, Vertex... children) {
		return new Vertex(id, distance, children);
	}

	private static Visualization visualization(Coordinate... coordinates) {
		return new Visualization(PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID, Radial.NAME, coordinates);
	}

	@Before
	public void init() {
		uri = neo4j.boltURI();
		config = Config.builder().withoutEncryption().build();
		database = neo4j.defaultDatabaseService();
		repository = new VisualizationRepository(database);
	}

	@Test
	public void read_inference1Edge() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx,"visualization", "ctx-1e.cypher");
			Tree result = repository.read(tx, PROJECT_ID, DATASET_ID, INFERENCE_ID);
			Vertex child = vertex(PROFILE2_ID, 2);
			Vertex expected = vertex(PROFILE1_ID, 0, child);
			assertEquals(new Tree(new Vertex[]{expected}), result);
			tx.rollback();
		}
	}

	@Test
	public void read_inferenceNEdge1Root() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "visualization", "ctx-ne-1r.cypher");
			Tree result = repository.read(tx, PROJECT_ID, DATASET_ID, INFERENCE_ID);
			Vertex grandChild = vertex(PROFILE3_ID, 3);
			Vertex child = vertex(PROFILE2_ID, 2, grandChild);
			Vertex expected = vertex(PROFILE1_ID, 0, child);
			assertEquals(new Tree(new Vertex[]{expected}), result);
			tx.rollback();
		}
	}

	@Test
	public void read_inferenceNEdge1Tree() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "visualization", "ctx-ne-1t.cypher");
			Tree result = repository.read(tx, PROJECT_ID, DATASET_ID, INFERENCE_ID);
			Vertex grandChild = vertex(PROFILE3_ID, 3);
			Vertex child1 = vertex(PROFILE2_ID, 2, grandChild);
			Vertex child2 = vertex(PROFILE4_ID, 1);
			Vertex expected = vertex(PROFILE1_ID, 0, child1, child2);
			assertEquals(new Tree(new Vertex[]{expected}), result);
			tx.rollback();
		}
	}

	@Test
	public void read_inferenceNEdgeNTree() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "visualization", "ctx-ne-nt.cypher");
			Tree result = repository.read(tx, PROJECT_ID, DATASET_ID, INFERENCE_ID);
			Vertex grandChild11 = vertex(PROFILE3_ID, 3);
			Vertex child1 = vertex(PROFILE2_ID, 2, grandChild11);
			Vertex grandChild21 = vertex(PROFILE5_ID, 1);
			Vertex grandChild22 = vertex(PROFILE6_ID, 1);
			Vertex child2 = vertex(PROFILE4_ID, 1, grandChild21, grandChild22);
			Vertex expected = vertex(PROFILE1_ID, 0, child1, child2);
			assertEquals(new Tree(new Vertex[]{expected}), result);
			tx.rollback();
		}
	}

	@Test
	public void write_2Coordinates() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "visualization", "ctx-ne-nt.cypher");
			long relationshipsCount = getRelationshipsCount(tx);
			long nodesCount = getNodesCount(tx);
			Coordinate c1 = new Coordinate(PROFILE1_ID, 1, 1, 2);
			Coordinate c2 = new Coordinate(PROFILE2_ID, 1, 3, 2.5);
			Visualization visualization = visualization(c1, c2);
			repository.write(tx, visualization);
			assertEquals(relationshipsCount + 2, getRelationshipsCount(tx));
			assertEquals(nodesCount + 2, getNodesCount(tx));
			assertVisualization(tx, visualization);
			tx.rollback();
		}
	}

	@Test
	public void write_nCoordinates() throws IOException {
		try (Transaction tx = database.beginTx()) {
			arrange(tx, "visualization", "ctx-ne-nt.cypher");
			long relationshipsCount = getRelationshipsCount(tx);
			long nodesCount = getNodesCount(tx);
			Coordinate c1 = new Coordinate(PROFILE1_ID, 1, 1, 2);
			Coordinate c2 = new Coordinate(PROFILE2_ID, 1, 3, 2.5);
			Coordinate c3 = new Coordinate(PROFILE3_ID, 1, 1.1, 10.13);
			Coordinate c4 = new Coordinate(PROFILE4_ID, 1, 0, 0);
			Visualization visualization = visualization(c1, c2, c3, c4);
			repository.write(tx, visualization);
			assertEquals(relationshipsCount + 4, getRelationshipsCount(tx));
			assertEquals(nodesCount + 4, getNodesCount(tx));
			assertVisualization(tx, visualization);
			tx.rollback();
		}
	}

	private void assertVisualization(Transaction tx, Visualization visualization) {
		List<Relationship> coordinates = tx.getAllRelationships().stream()
				.filter(r -> r.getType().equals(RelationshipType.withName(Relation.HAS.name())))
				.collect(Collectors.toList());
		assertEquals(visualization.getCoordinates().length, coordinates.size());
		for (int i = 0; i < coordinates.size(); i++) {
			Relationship r = coordinates.get(i);
			String profileId = r.getStartNode().getProperty(Profile.ID).toString();
			Optional<Coordinate> coordinate = Arrays.stream(visualization.getCoordinates()).filter(c -> c.getProfileId().equals(profileId))
					.findFirst();
			assertTrue(coordinate.isPresent());
			Coordinate c = coordinate.get();
			assertEquals(r.getEndNode().getProperty(algorithm.utils.type.Coordinate.X), c.getX());
			assertEquals(r.getEndNode().getProperty(algorithm.utils.type.Coordinate.Y), c.getY());
			assertEquals(r.getProperty(Has.INFERENCE_ID), visualization.getInferenceId());
			assertEquals(r.getProperty(Has.ID), visualization.getId());
			assertEquals(r.getProperty(Has.COMPONENT), c.getComponent());
			assertEquals(r.getProperty(Has.ALGORITHM), visualization.getAlgorithm());
			assertEquals(r.getProperty(Has.DEPRECATED), false);
		}
	}
}
