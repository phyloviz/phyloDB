package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;

import java.util.Objects;

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
