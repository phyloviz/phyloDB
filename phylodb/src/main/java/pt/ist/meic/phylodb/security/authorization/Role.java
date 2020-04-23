package pt.ist.meic.phylodb.security.authorization;

import java.util.Arrays;

public enum Role {

	ADMIN, USER;

	public static boolean exists(String name) {
		return Arrays.stream(Role.values())
				.map(Role::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}

}
