package algorithm.inference;

import algorithm.inference.implementation.GoeBURST;
import algorithm.inference.model.Edge;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class GoeBURSTTests {

	private static final String PROJECT_ID = "project", DATASET_ID = "dataset", INFERENCE_ID = "inference",
			PROFILE1_ID = "1", PROFILE2_ID = "2", PROFILE3_ID = "3";

	private GoeBURST goeburst;

	@Before
	public void init() {
		goeburst = new GoeBURST();
		goeburst.init(PROJECT_ID, DATASET_ID, INFERENCE_ID, 3L);
	}

	@Test
	public void compute_matrix2x2() {
		String[] ids = {PROFILE1_ID, PROFILE2_ID};
		int[] isolates = new int[ids.length];
		String[][] allelesIds = {{"1", "2"}, {"2", "1"}};
		Matrix matrix = new Matrix(ids, isolates, allelesIds);
		List<Edge> expected = new ArrayList<>();
		expected.add(new Edge(0, 1, 2));
		Inference inference = goeburst.compute(matrix);
		assertEdges(expected, ids, inference);
	}

	@Test
	public void compute_matrix3x3() {
		String[] ids = {PROFILE1_ID, PROFILE2_ID, PROFILE3_ID};
		int[] isolates = new int[ids.length];
		String[][] allelesIds = {{"1", "2"}, {"1", "1"}, {"2", "1"}};
		Matrix matrix = new Matrix(ids, isolates, allelesIds);
		List<Edge> expected = new ArrayList<>();
		expected.add(new Edge(0, 1, 1));
		expected.add(new Edge(1, 2, 1));
		Inference inference = goeburst.compute(matrix);
		assertEdges(expected, ids, inference);
	}

	@Test
	public void compute_matrix8x3() {
		String[] ids = {"1", "2", "3", "4", "5", "6", "7", "8"};
		int[] isolates = new int[ids.length];
		String[][] allelesIds = {{"1", "1", "1"}, {"2", "2", "2"}, {"4", "6", "3"}, {"4", "3", "4"},
				{"3", "7", "5"}, {"5", "4", "6"}, {"2", "8", "7"}, {"6", "5", "8"}};
		Matrix matrix = new Matrix(ids, isolates, allelesIds);
		List<Edge> expected = new ArrayList<>();
		expected.add(new Edge(0, 1, 3));
		expected.add(new Edge(1, 2, 3));
		expected.add(new Edge(1, 4, 3));
		expected.add(new Edge(1, 5, 3));
		expected.add(new Edge(1, 6, 2));
		expected.add(new Edge(1, 7, 3));
		expected.add(new Edge(2, 3, 2));
		Inference inference = goeburst.compute(matrix);
		assertEdges(expected, ids, inference);
	}

	private void assertEdges(List<Edge> expected, String[] ids, Inference inference) {
		assertEquals(PROJECT_ID, inference.getProjectId());
		assertEquals(DATASET_ID, inference.getDatasetId());
		assertEquals(INFERENCE_ID, inference.getId());
		assertTrue(Arrays.asList(inference.getProfileIds()).containsAll(Arrays.asList(ids)));
		assertEquals(inference.getEdges().size(), expected.size());
		assertTrue(inference.getEdges().containsAll(expected));
	}

}
