package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;

import java.util.Objects;

public class GetAnalysisOutputModel extends AnalysisOutputModel {

	private String tree;

	public GetAnalysisOutputModel() {
	}

	public GetAnalysisOutputModel(Analysis analysis, String format) {
		super(analysis);
		this.tree = TreeFormatter.get(format).format(analysis.getEdges());
	}

	public String getTree() {
		return tree;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetAnalysisOutputModel that = (GetAnalysisOutputModel) o;
		return Objects.equals(tree, that.tree);
	}

}
