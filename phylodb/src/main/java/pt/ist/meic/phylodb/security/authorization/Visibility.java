package pt.ist.meic.phylodb.security.authorization;

import java.util.Arrays;

/**
 * A Role represents the type of visibility that a project can have
 */
public enum Visibility {

	PUBLIC, PRIVATE;

	/**
	 * Verifies that the parameter name is a Visibility
	 *
	 * @param name name of the algorithm
	 * @return {@code true} if the name received exists as a Visibility, {@code false} otherwise
	 */
	public static boolean exists(String name) {
		return Arrays.stream(Visibility.values())
				.map(Visibility::getName)
				.anyMatch(n -> n.equals(name));
	}

	/**
	 * Retrieves the lowercase name of the Visibility
	 *
	 * @return lowercase name of the Visibility
	 */
	public String getName() {
		return name().toLowerCase();
	}
}
