package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class AlleleOutputModel extends SingleOutputModel {

	private final String sequence;

	public AlleleOutputModel(Allele allele) {
		super(allele.getPrimaryKey().getId(), allele.getVersion(), allele.isDeprecated());
		this.sequence = allele.getSequence();
	}

	public String getSequence() {
		return sequence;
	}

}
