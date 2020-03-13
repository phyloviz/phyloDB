package pt.ist.meic.phylodb.phylogeny.taxon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TaxonInputModel implements Serializable {

	@JsonProperty(required = true)
	private String id;
	private String description;

	public TaxonInputModel() {
	}

	public TaxonInputModel(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

}
