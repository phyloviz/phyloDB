package pt.ist.meic.phylodb.security.authorization;

import java.util.Arrays;

/**
 * A Role represents the type of roles that a user can have
 */
public enum Role {

	ADMIN, USER;

	/**
	 * Verifies that the parameter name is a Role
	 *
	 * @param name name of the algorithm
	 * @return {@code true} if the name received exists as a Role, {@code false} otherwise
	 */
	public static boolean exists(String name) {
		return Arrays.stream(Role.values())
				.map(Role::getName)
				.anyMatch(n -> n.equals(name));
	}

	/**
	 * Retrieves the lowercase name of the Role
	 *
	 * @return lowercase name of the Role
	 */
	public String getName() {
		return name().toLowerCase();
	}

}
