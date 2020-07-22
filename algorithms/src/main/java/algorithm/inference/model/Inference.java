package algorithm.inference.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * An inference is the result of an inference study executed on a dataset. It contains a list of {@link Edge edges} that represent the result of the inference
 * <p>
 * An inference is constituted by the {@link #projectId}, {@link #datasetId}, and {@link #id} fields to identify the inference,
 * the {@link #profileIds} that contains the real profile ids, the {@link #algorithm} field that is the algorithm used to produce the inference, and by the {@link #edges} field which are the edges that compose this inference.
 * An inference results of an inference algorithm execution.
 */
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
