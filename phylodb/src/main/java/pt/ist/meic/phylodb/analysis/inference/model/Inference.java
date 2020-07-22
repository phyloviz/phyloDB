package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Objects;

/**
 * An inference is the result of an inference study executed on a dataset. It contains a list of {@link Edge edges} that represent the result of the inference
 * <p>
 * An inference is constituted by the {@link #id} field to identify the inference, the {@link #deprecated} field which indicates if the inference is deprecated,
 * the {@link #algorithm} field that is the algorithm used to produce the inference, and by the {@link #edges} field which are the edges that compose this inference.
 * An inference results of an inference algorithm execution.
 */
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

	/**
	 * An Inference.PrimaryKey is the identification of an inference
	 * <p>
	 * An Inference.PrimaryKey is constituted by the {@link #projectId}, {@link #datasetId}, and {@link #id} fields which identify the inference.
	 */
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
