package pt.ist.meic.phylodb.analysis.inference.model;

import java.util.Arrays;

public enum InferenceAlgorithm {

	GOEBURST;

	public static boolean exists(String name) {
		return Arrays.stream(InferenceAlgorithm.values())
				.map(InferenceAlgorithm::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
