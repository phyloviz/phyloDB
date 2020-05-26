package pt.ist.meic.phylodb.analysis.visualization.model;

import java.util.List;
import java.util.Objects;

public class Visualization {

	private final PrimaryKey primaryKey;
	private final boolean deprecated;
	private final VisualizationAlgorithm algorithm;
	private final List<Coordinate> coordinates;

	public Visualization(String projectId, String datasetId, String analysisId, String id, boolean deprecated, VisualizationAlgorithm algorithm, List<Coordinate> coordinates) {
		this.primaryKey = new PrimaryKey(projectId, datasetId, analysisId, id);
		this.deprecated = deprecated;
		this.algorithm = algorithm;
		this.coordinates = coordinates;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public boolean isDeprecated() {
		return deprecated;
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
		Visualization that = (Visualization) o;
		return Objects.equals(primaryKey, that.primaryKey) &&
				algorithm == that.algorithm &&
				Objects.equals(coordinates, that.coordinates);
	}

	public static class PrimaryKey {

		private final String projectId;
		private final String datasetId;
		private final String analysisId;
		private final String id;

		public PrimaryKey(String projectId, String datasetId, String analysisId, String id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.analysisId = analysisId;
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getDatasetId() {
			return datasetId;
		}

		public String getAnalysisId() {
			return analysisId;
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
					Objects.equals(analysisId, that.analysisId) &&
					Objects.equals(id, that.id);
		}

	}
}
