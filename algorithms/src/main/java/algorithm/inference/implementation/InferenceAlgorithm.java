package algorithm.inference.implementation;

import algorithm.utils.Algorithm;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;

/**
 * Base class for an inference algorithm, contains the state associated with an inference
 */
public abstract class InferenceAlgorithm implements Algorithm<Matrix, Inference> {

	protected String projectId;
	protected String datasetId;
	protected String id;

}
