package pt.ist.meic.phylodb.analysis.inference.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Analysis {

	private final PrimaryKey primaryKey;
	private final boolean deprecated;
	private final InferenceAlgorithm algorithm;
	private final List<Edge> edges;

	public Analysis(UUID projectId, UUID datasetId, UUID id, InferenceAlgorithm algorithm, boolean deprecated, List<Edge> edges) {
		this.primaryKey = new PrimaryKey(projectId, datasetId, id);
		this.deprecated = deprecated;
		this.algorithm = algorithm;
		this.edges = edges;
	}

	public Analysis(UUID projectId, UUID datasetId, UUID id, InferenceAlgorithm algorithm, List<Edge> edges) {
		this(projectId, datasetId, id, algorithm, false, edges);
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public boolean isDeprecated() {
		return deprecated;
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
		Analysis analysis = (Analysis) o;
		return deprecated == analysis.deprecated &&
				Objects.equals(primaryKey, analysis.primaryKey) &&
				algorithm == analysis.algorithm &&
				Objects.equals(edges, analysis.edges);
	}

	public static class PrimaryKey {

		private final UUID projectId;
		private final UUID datasetId;
		private final UUID id;

		public PrimaryKey(UUID projectId, UUID datasetId, UUID id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.id = id;
		}

		public UUID getProjectId() {
			return projectId;
		}

		public UUID getDatasetId() {
			return datasetId;
		}

		public UUID getId() {
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
