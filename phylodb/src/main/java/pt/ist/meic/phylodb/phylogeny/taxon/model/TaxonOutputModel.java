package pt.ist.meic.phylodb.phylogeny.taxon.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class TaxonOutputModel extends SingleOutputModel {

	private final String description;

	public TaxonOutputModel(Taxon taxon) {
		super(taxon.getPrimaryKey(), taxon.getVersion(), taxon.isDeprecated());
		this.description = taxon.getDescription();
	}

	public String getDescription() {
		return description;
	}

}
