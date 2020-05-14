package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.typing.Method;

import java.util.Arrays;

public enum VisualizationAlgorithm {

	FORCE_DIRECTED_LAYOUT;

	public static boolean exists(String name) {
		return Arrays.stream(Method.values())
				.map(Method::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
