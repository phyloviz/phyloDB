package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;

import java.util.Objects;

/**
 * A GetInferenceOutputModel is the output model representation of an {@link Inference}
 * <p>
 * A GetInferenceOutputModel is constituted by the {@link #project_id}, {@link #dataset_id}, and {@link #id} fields to identify the inference.
 * It also contains the {@link #deprecated} field which indicates if the inference is deprecated, and the {@link #algorithm} and {@link #tree}
 * which are the algorithm that was used to produce this inference and the resultant tree, respectively.
 */
public class GetInferenceOutputModel extends InferenceOutputModel {

	private String algorithm;
	private String tree;

	public GetInferenceOutputModel() {
	}

	public GetInferenceOutputModel(Inference analysis, String format) {
		super(analysis);
		this.algorithm = analysis.getAlgorithm().getName();
		this.tree = TreeFormatter.get(format).format(analysis.getEdges());
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getTree() {
		return tree;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetInferenceOutputModel that = (GetInferenceOutputModel) o;
		return Objects.equals(tree, that.tree);
	}

}
