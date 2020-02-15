package pt.ist.meic.phylodb.phylogeny.taxon.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GetTaxonOutputModel {

	private String id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GetTaxonOutputModel() {
	}

	public GetTaxonOutputModel(Taxon taxon) {
		this.id = taxon.get_id();
		this.description = taxon.getDescription();
	}
}
