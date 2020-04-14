package pt.ist.meic.phylodb.security.authorization;

import pt.ist.meic.phylodb.typing.Method;

import java.util.Arrays;

public enum Role {

	ADMIN, USER;

	public static boolean exists(String name) {
		return Arrays.stream(Method.values())
				.map(Method::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
