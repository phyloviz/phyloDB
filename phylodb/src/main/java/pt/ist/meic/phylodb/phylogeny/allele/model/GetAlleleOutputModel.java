package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class AlleleOutputModel extends SingleOutputModel<Allele.PrimaryKey> {

	private final String sequence;

	public AlleleOutputModel(Allele allele) {
		super(allele);
		this.sequence = allele.getSequence();
	}

	public String getSequence() {
		return sequence;
	}

}
