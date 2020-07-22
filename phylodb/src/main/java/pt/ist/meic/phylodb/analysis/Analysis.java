package pt.ist.meic.phylodb.analysis;

import java.util.Arrays;

/**
 * Analysis enum contains all the types of analysis supported
 */
public enum Analysis {

	INFERENCE, VISUALIZATION;

	/**
	 * Verifies that the parameter name is an Analysis
	 *
	 * @param name name of the algorithm
	 * @return {@code true} if the name received exists as an Analysis
	 */
	public static boolean exists(String name) {
		return Arrays.stream(Analysis.values())
				.map(Analysis::getName)
				.anyMatch(n -> n.equals(name));
	}

	/**
	 * Retrieves the lowercase name of the Analysis
	 *
	 * @return lowercase name of the Analysis
	 */
	public String getName() {
		return name().toLowerCase();
	}

}
