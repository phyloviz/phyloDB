package algorithm.visualization.model;

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
