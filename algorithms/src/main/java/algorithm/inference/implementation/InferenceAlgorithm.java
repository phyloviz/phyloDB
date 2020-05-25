package algorithm.inference.implementation;

import algorithm.Algorithm;
import algorithm.inference.model.Inference;
import algorithm.inference.model.Matrix;

public abstract class InferenceAlgorithm implements Algorithm<Matrix, Inference> {

	protected String projectId;
	protected String datasetId;
	protected String id;
}
