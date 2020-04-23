package pt.ist.meic.phylodb.phylogeny.taxon.model;

import java.util.Objects;

public class GetTaxonOutputModel extends TaxonOutputModel {

	private String description;

	public GetTaxonOutputModel() { }

	public GetTaxonOutputModel(Taxon taxon) {
		super(taxon);
		this.description = taxon.getDescription();
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GetTaxonOutputModel that = (GetTaxonOutputModel) o;
		return  super.equals(that) &&
				Objects.equals(description, that.description);
	}

}
