package pt.ist.meic.phylodb.typing.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaInputModel {

	@JsonProperty(required = true)
	private String taxon;
	@JsonProperty(required = true)
	private String id;
	private String description;
	private String[] loci;

	public SchemaInputModel() {
	}

	public SchemaInputModel(String taxon, String id, String description, String[] loci) {
		this.id = id;
		this.description = description;
		this.taxon = taxon;
		this.loci = loci;
	}

	public String getTaxon() {
		return taxon;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String[] getLoci() {
		return loci;
	}

}
