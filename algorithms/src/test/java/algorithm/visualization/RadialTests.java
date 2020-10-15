package algorithm.visualization;

import algorithm.visualization.implementation.Radial;
import algorithm.visualization.model.Coordinate;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Vertex;
import algorithm.visualization.model.Visualization;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RadialTests {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset", INFERENCE_ID = "inference",
			VISUALIZATION_ID = "visualization", PROFILE1_ID = "1", PROFILE2_ID = "2", PROFILE3_ID = "3",
			PROFILE4_ID = "4", PROFILE5_ID = "5", PROFILE6_ID = "6";

	private Radial radial;

	private static Vertex vertex(String id, int distance, Vertex... children) {
		return new Vertex(id, distance, children);
	}

	@Before
	public void init() {
		radial = new Radial();
		radial.init(PROJECT_ID, DATASET_ID, INFERENCE_ID, VISUALIZATION_ID);
	}

	@Test
	public void compute_1Edges() {
		Coordinate[] coordinates = new Coordinate[]{new Coordinate(PROFILE1_ID, 1, 0, 0), new Coordinate(PROFILE2_ID, 1, -210, 2.5717582782094416e-14)};
		Vertex child = vertex(PROFILE2_ID, 2);
		Vertex root = vertex(PROFILE1_ID, 0, child);
		Visualization visualization = radial.compute(new Tree(new Vertex[]{root}));
		assertVisualization(coordinates, visualization);
	}

	@Test
	public void compute_nEdges1Root() {
		Coordinate[] coordinates = new Coordinate[]{
				new Coordinate(PROFILE1_ID, 1, 0, 0), new Coordinate(PROFILE2_ID, 1, -210, 2.5717582782094416e-14),
				new Coordinate(PROFILE3_ID, 1, -520, 6.368163355566237e-14)
		};
		Vertex grandChild = vertex(PROFILE3_ID, 3);
		Vertex child = vertex(PROFILE2_ID, 2, grandChild);
		Vertex root = vertex(PROFILE1_ID, 0, child);
		Visualization visualization = radial.compute(new Tree(new Vertex[]{root}));
		assertVisualization(coordinates, visualization);
	}

	@Test
	public void compute_nEdgesNTree() {
		Coordinate[] coordinates = new Coordinate[]{
			new Coordinate(PROFILE1_ID,  1, 0, 0), new Coordinate(PROFILE2_ID, 1, 105.00000000000003, 181.8653347947321),
			new Coordinate(PROFILE3_ID,  1, 260.00000000000006, 450.33320996790803), new Coordinate(PROFILE4_ID, 1, -255.00000000000023, -441.67295593006355),
			new Coordinate(PROFILE5_ID, 1, -365.0000000000002, -441.67295593006355), new Coordinate(PROFILE6_ID, 1, -200.00000000000028, -536.9357503463518)
		};
		Vertex grandChild11 = vertex(PROFILE3_ID, 3);
		Vertex child1 = vertex(PROFILE2_ID, 2, grandChild11);
		Vertex grandChild21 = vertex(PROFILE5_ID, 1);
		Vertex grandChild22 = vertex(PROFILE6_ID, 1);
		Vertex child2 = vertex(PROFILE4_ID, 5, grandChild21, grandChild22);
		Vertex root = vertex(PROFILE1_ID, 0, child1, child2);
		Visualization visualization = radial.compute(new Tree(new Vertex[]{root}));
		assertVisualization(coordinates, visualization);
	}

	private void assertVisualization(Coordinate[] coordinates, Visualization visualization) {
		assertEquals(PROJECT_ID, visualization.getProjectId());
		assertEquals(DATASET_ID, visualization.getDatasetId());
		assertEquals(INFERENCE_ID, visualization.getInferenceId());
		assertEquals(VISUALIZATION_ID, visualization.getId());
		assertEquals(Radial.NAME, visualization.getAlgorithm());
		assertArrayEquals(coordinates, visualization.getCoordinates());
	}

}
