package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.Objects;

/**
 * A Visualization is the result of an visualization algorithm executed on a inference study. It contains a list of {@link Coordinate coordinates} that represent the result of the visualization algorithm
 * <p>
 * A Visualization is constituted by the {@link #id} field to identify the visualization, the {@link #deprecated} field which indicates if the visualization is deprecated,
 * the {@link #algorithm} field that is the algorithm used to produce the visualization, and by the {@link #coordinates} field which are the coordinates that compose this visualization.
 * An Visualization results of a visualization algorithm execution.
 */
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

	/**
	 * A Visualization.PrimaryKey is the identification of an visualization
	 * <p>
	 * A Visualization.PrimaryKey is constituted by the {@link #projectId}, {@link #datasetId}, {@link #inferenceId}, and {@link #id} fields which identify the visualization.
	 */
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
