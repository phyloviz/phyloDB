package pt.ist.meic.phylodb.security.authorization;

import java.util.Arrays;

public enum Visibility {

	PUBLIC, PRIVATE;

	public static boolean exists(String name) {
		return Arrays.stream(Visibility.values())
				.map(Visibility::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
