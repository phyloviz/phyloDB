package pt.ist.meic.phylodb.typing.dataset.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDatasetInputModel {

	private String description;
	@JsonProperty(required = true)
	private String taxonId;
	@JsonProperty(required = true)
	private String schemaId;

	public PostDatasetInputModel() {
	}

	public PostDatasetInputModel(String description, String taxonId, String schemaId) {
		this.description = description;
		this.taxonId = taxonId;
		this.schemaId = schemaId;
	}


	public String getDescription() {
		return description;
	}

	public String getTaxonId() {
		return taxonId;
	}

	public String getSchemaId() {
		return schemaId;
	}
}
