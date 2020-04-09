package pt.ist.meic.phylodb.typing;

import java.util.Arrays;

public enum Method {

	MLST, MLVA, SNP;

	public static boolean exists(String name) {
		return Arrays.stream(Method.values())
				.map(Method::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}
}
