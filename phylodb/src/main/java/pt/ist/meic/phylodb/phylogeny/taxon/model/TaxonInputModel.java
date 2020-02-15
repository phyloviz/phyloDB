package pt.ist.meic.phylodb.phylogeny.taxon.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

public class TaxonInputModel implements Serializable {

	private String id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public TaxonInputModel() {
	}

	public TaxonInputModel(String id, String description) {
		this.id = id;
		this.description = description;
	}
}
