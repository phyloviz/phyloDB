package pt.ist.meic.phylodb.typing.dataset.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PutDatasetInputModel {

	@JsonProperty(required = true)
	private UUID id;
	@JsonProperty(required = true)
	private String description;

	public PutDatasetInputModel() {
	}

	public PutDatasetInputModel(UUID id, String description) {
		this.id = id;
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

}
