package pt.ist.meic.phylodb.file.graph;


public class Edge {

	private String value;
	private Vertice to;

	public Edge(String value, Vertice to) {
		this.value = value;
		this.to = to;
	}
}
