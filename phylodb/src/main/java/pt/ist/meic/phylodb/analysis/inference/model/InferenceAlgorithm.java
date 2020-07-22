package pt.ist.meic.phylodb.analysis.inference.model;

import java.util.Arrays;

/**
 * InferenceAlgorithm enum contains all the inference algorithms supported
 */
public enum InferenceAlgorithm {

	GOEBURST;

	/**
	 * Verifies that the parameter name is an InferenceAlgorithm
	 *
	 * @param name name of the algorithm
	 * @return {@code true} if the name received exists as an InferenceAlgorithm
	 */
	public static boolean exists(String name) {
		return Arrays.stream(InferenceAlgorithm.values())
				.map(InferenceAlgorithm::getName)
				.anyMatch(n -> n.equals(name));
	}

	/**
	 * Retrieves the lowercase name of the InferenceAlgorithm
	 *
	 * @return lowercase name of the InferenceAlgorithm
	 */
	public String getName() {
		return name().toLowerCase();
	}
}
