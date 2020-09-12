package algorithm.visualization.implementation;

import algorithm.utils.Algorithm;
import algorithm.visualization.model.Tree;
import algorithm.visualization.model.Visualization;

/**
 * Base class for a visualization algorithm, contains the state associated with a visualization
 */
public abstract class VisualizationAlgorithm implements Algorithm<Tree, Visualization> {

	protected String projectId;
	protected String datasetId;
	protected String inferenceId;
	protected String id;

}
