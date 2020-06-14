package algorithm.visualization.implementation;

import algorithm.Algorithm;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Visualization;

public abstract class VisualizationAlgorithm implements Algorithm<Tree, Visualization> {

	protected String projectId;
	protected String datasetId;
	protected String inferenceId;
	protected String id;
}
