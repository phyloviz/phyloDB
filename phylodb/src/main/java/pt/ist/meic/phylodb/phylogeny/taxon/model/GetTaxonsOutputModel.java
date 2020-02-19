package pt.ist.meic.phylodb.phylogeny.taxon.model;

import java.util.List;
import java.util.stream.Collectors;

public class GetTaxonsOutputModel {

	private List<GetTaxonOutputModel> taxons;

	public GetTaxonsOutputModel() {
	}

	public GetTaxonsOutputModel(List<Taxon> taxons) {
		this.taxons = taxons.stream()
				.map(GetTaxonOutputModel::new)
				.collect(Collectors.toList());
	}

	public List<GetTaxonOutputModel> getTaxons() {
		return taxons;
	}

	public void setTaxons(List<GetTaxonOutputModel> taxons) {
		this.taxons = taxons;
	}

}
