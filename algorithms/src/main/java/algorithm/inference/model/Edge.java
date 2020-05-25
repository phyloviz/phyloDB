package algorithm.inference.model;

import java.util.Objects;

public class Edge {

	private final int from;
	private final int to;
	private final int distance;

	public Edge(int from, int to, int distance) {
		this.from = from;
		this.to = to;
		this.distance = distance;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public int distance() {
		return distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Edge edge = (Edge) o;
		return from == edge.from &&
				to == edge.to &&
				distance == edge.distance;
	}

}
