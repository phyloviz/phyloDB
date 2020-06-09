package pt.ist.meic.phylodb.analysis.visualization.model;

import java.util.Arrays;

public enum VisualizationAlgorithm {

	RADIAL;

	public static boolean exists(String name) {
		return Arrays.stream(VisualizationAlgorithm.values())
				.map(VisualizationAlgorithm::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
