package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Analysis extends Entity<Analysis.PrimaryKey> {

	private final InferenceAlgorithm algorithm;
	private final List<Edge> edges;

	public Analysis(UUID projectId, UUID datasetId, UUID id, long version, boolean deprecated, InferenceAlgorithm algorithm, List<Edge> edges) {
		super(new PrimaryKey(projectId, datasetId, id, algorithm), version, deprecated);
		this.algorithm = algorithm;
		this.edges = edges;
	}

	public Analysis(UUID projectId, UUID datasetId, UUID id, InferenceAlgorithm algorithm, List<Edge> edges) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, algorithm, edges);
	}

	public InferenceAlgorithm getAlgorithm() {
		return algorithm;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public static class PrimaryKey {

		private final UUID projectId;
		private final UUID datasetId;
		private final UUID id;
		private final InferenceAlgorithm algorithm;

		public PrimaryKey(UUID projectId, UUID datasetId, UUID id, InferenceAlgorithm algorithm) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.id = id;
			this.algorithm = algorithm;
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

		public InferenceAlgorithm getAlgorithm() {
			return algorithm;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(projectId, that.projectId) &&
					Objects.equals(datasetId, that.datasetId) &&
					Objects.equals(id, that.id) &&
					algorithm == that.algorithm;
		}
	}

}
