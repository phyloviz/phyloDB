package algorithm.visualization.model;

/**
 * A Visualization is the result of an {@link algorithm.visualization.implementation.VisualizationAlgorithm} executed on a inference study. It contains a list of {@link Coordinate coordinates} that represent the result of the visualization algorithm
 * <p>
 * A Visualization is constituted by the {@link #projectId}, {@link #datasetId}, {@link #inferenceId}, {@link #id} fields to identify the visualization,
 * the {@link #algorithm} field that is the algorithm used to produce the visualization, and by the {@link #coordinates} field which are the coordinates that compose this visualization.
 * An Visualization results of a visualization algorithm execution.
 */
public class Visualization {

	private final String projectId;
	private final String datasetId;
	private final String inferenceId;
	private final String id;
	private final String algorithm;
	private final Coordinate[] coordinates;

	public Visualization(String projectId, String datasetId, String inferenceId, String id, String algorithm, Coordinate[] coordinates) {
		this.projectId = projectId;
		this.datasetId = datasetId;
		this.inferenceId = inferenceId;
		this.id = id;
		this.algorithm = algorithm;
		this.coordinates = coordinates;
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

	public String getAlgorithm() {
		return algorithm;
	}

	public Coordinate[] getCoordinates() {
		return coordinates;
	}

}
