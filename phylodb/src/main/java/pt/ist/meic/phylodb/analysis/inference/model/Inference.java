package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Objects;


public class Inference extends Entity<Inference.PrimaryKey> {

	private final InferenceAlgorithm algorithm;
	private final List<Edge> edges;

	public Inference(String projectId, String datasetId, String id, boolean deprecated, InferenceAlgorithm algorithm, List<Edge> edges) {
		super(new PrimaryKey(projectId, datasetId, id), deprecated);
		this.algorithm = algorithm;
		this.edges = edges;
	}

	public Inference(String projectId, String datasetId, String id, InferenceAlgorithm algorithm, List<Edge> edges) {
		this(projectId, datasetId, id, false, algorithm, edges);
	}

	public InferenceAlgorithm getAlgorithm() {
		return algorithm;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Inference inference = (Inference) o;
		return algorithm == inference.algorithm &&
				Objects.equals(edges, inference.edges);
	}

	public static class PrimaryKey {

		private final String projectId;
		private final String datasetId;
		private final String id;

		public PrimaryKey(String projectId, String datasetId, String id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.id = id;
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(projectId, that.projectId) &&
					Objects.equals(datasetId, that.datasetId) &&
					Objects.equals(id, that.id);
		}
	}

}
