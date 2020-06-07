package algorithm.visualization.model;

public class Vertex {

	private String id;
	private int distance;
	private Vertex[] children;

	public Vertex(String id, int distance, Vertex[] children) {
		this.id = id;
		this.distance = distance;
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

	public Vertex[] getChildren() {
		return children;
	}

}
