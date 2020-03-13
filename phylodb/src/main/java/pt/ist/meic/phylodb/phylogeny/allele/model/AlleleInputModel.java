package pt.ist.meic.phylodb.phylogeny.allele.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlleleInputModel {

	@JsonProperty(required = true)
	private String id;
	private String sequence;

	public AlleleInputModel() {
	}

	public AlleleInputModel(String id, String description) {
		this.id = id;
		this.sequence = description;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}

}
