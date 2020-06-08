package algorithm.visualization.model;

import java.util.Arrays;
import java.util.Objects;

public class Vertex {

	private final String id;
	private final long distance;
	private final Vertex[] children;

	public Vertex(String id, long distance, Vertex[] children) {
		this.id = id;
		this.distance = distance;
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public long getDistance() {
		return distance;
	}

	public Vertex[] getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vertex vertex = (Vertex) o;
		return distance == vertex.distance &&
				Objects.equals(id, vertex.id) &&
				Arrays.equals(children, vertex.children);
	}

}
