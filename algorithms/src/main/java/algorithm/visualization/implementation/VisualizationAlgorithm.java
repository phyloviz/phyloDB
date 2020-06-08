package algorithm.visualization.implementation;

import algorithm.Algorithm;
import algorithm.visualization.model.Vertex;
import algorithm.visualization.model.Visualization;

public abstract class VisualizationAlgorithm implements Algorithm<Vertex, Visualization> {

	protected String projectId;
	protected String datasetId;
	protected String inferenceId;
	protected String id;
}
