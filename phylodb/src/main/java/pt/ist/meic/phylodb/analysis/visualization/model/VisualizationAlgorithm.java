package pt.ist.meic.phylodb.analysis.visualization.model;

import java.util.Arrays;

/**
 * VisualizationAlgorithm enum contains all the visualization algorithms supported
 */
public enum VisualizationAlgorithm {

	RADIAL;

	/**
	 * Verifies that the parameter name is an VisualizationAlgorithm
	 *
	 * @param name name of the algorithm
	 * @return {@code true} if the name received exists as an VisualizationAlgorithm
	 */
	public static boolean exists(String name) {
		return Arrays.stream(VisualizationAlgorithm.values())
				.map(VisualizationAlgorithm::getName)
				.anyMatch(n -> n.equals(name));
	}

	/**
	 * Retrieves the lowercase name of the VisualizationAlgorithm
	 *
	 * @return lowercase name of the VisualizationAlgorithm
	 */
	public String getName() {
		return name().toLowerCase();
	}
}
