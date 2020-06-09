package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Objects;

public class Visualization extends Entity<Visualization.PrimaryKey> {

	private final VisualizationAlgorithm algorithm;
	private final List<Coordinate> coordinates;

	public Visualization(String projectId, String datasetId, String analysisId, String id, boolean deprecated, VisualizationAlgorithm algorithm, List<Coordinate> coordinates) {
		super(new PrimaryKey(projectId, datasetId, analysisId, id), deprecated);
		this.algorithm = algorithm;
		this.coordinates = coordinates;
	}

	public VisualizationAlgorithm getAlgorithm() {
		return algorithm;
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Visualization that = (Visualization) o;
		return algorithm == that.algorithm &&
				Objects.equals(coordinates, that.coordinates);
	}

	public static class PrimaryKey {

		private final String projectId;
		private final String datasetId;
		private final String inferenceId;
		private final String id;

		public PrimaryKey(String projectId, String datasetId, String inferenceId, String id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.inferenceId = inferenceId;
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getDatasetId() {
			return datasetId;
		}

		public String getInferenceId() {
			return inferenceId;
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
					Objects.equals(inferenceId, that.inferenceId) &&
					Objects.equals(id, that.id);
		}

	}
}
