package pt.ist.meic.phylodb.phylogeny.taxon.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GetTaxonOutputModel {

	private String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	public GetTaxonOutputModel() {
	}

	public GetTaxonOutputModel(Taxon taxon) {
		this.id = taxon.getId();
		this.description = taxon.getDescription();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
