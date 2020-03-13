package pt.ist.meic.phylodb.phylogeny.locus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocusInputModel {

	@JsonProperty(required = true)
	private String id;
	private String description;

	public LocusInputModel() {
	}

	public LocusInputModel(String id, String description) {
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
