package pt.ist.meic.phylodb.phylogeny.taxon.model;

import java.util.Objects;

/**
 * A GetTaxonOutputModel is the output model representation of an {@link Taxon}
 * <p>
 * A GetTaxonOutputModel is constituted by the {@link #id} field to identify the taxon,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the taxon is deprecated, and what version it has.
 * It also contains the {@link #description}, that is a description of this taxon.
 */
public class GetTaxonOutputModel extends TaxonOutputModel {

	private String description;

	public GetTaxonOutputModel() {
	}

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
		return super.equals(that) &&
				Objects.equals(description, that.description);
	}

}
