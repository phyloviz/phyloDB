package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class LocusOutputModel extends SingleOutputModel<Locus.PrimaryKey> {

	private final String description;

	public LocusOutputModel(Locus locus) {
		super(locus);
		this.description = locus.getDescription();
	}

	public String getDescription() {
		return description;
	}

}
