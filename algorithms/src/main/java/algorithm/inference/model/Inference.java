package algorithm.inference.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Inference {

	private final String projectId;
	private final String datasetId;
	private final String id;
	private final String[] profileIds;
	private final String algorithm;
	private final List<Edge> edges;

	public Inference(String projectId, String datasetId, String id, String[] profileIds, String algorithm) {
		this.projectId = projectId;
		this.datasetId = datasetId;
		this.id = id;
		this.profileIds = profileIds;
		this.algorithm = algorithm;
		this.edges = new ArrayList<>();
	}

	public String getProjectId() {
		return projectId;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String getId() {
		return id;
	}

	public String[] getProfileIds() {
		return profileIds;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public Stream<Edge> edges() {
		return edges.stream();
	}

	public void add(Edge edge) {
		edges.add(edge);
	}

}
