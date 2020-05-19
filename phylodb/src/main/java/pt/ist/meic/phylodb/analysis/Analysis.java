package pt.ist.meic.phylodb.analysis;

import java.util.Arrays;

public enum Analysis {

	INFERENCE, VISUALIZATION;

	public static boolean exists(String name) {
		return Arrays.stream(Analysis.values())
				.map(Analysis::getName)
				.anyMatch(n -> n.equals(name));
	}

	public String getName() {
		return name().toLowerCase();
	}

}
