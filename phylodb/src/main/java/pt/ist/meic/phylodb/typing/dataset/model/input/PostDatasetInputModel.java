package pt.ist.meic.phylodb.typing.dataset.model.input;

import pt.ist.meic.phylodb.input.Input;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

import java.util.Optional;
import java.util.UUID;

public class PostDatasetInputModel implements Input<Dataset> {

	private String description;
	private String taxonId;
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

	@Override
	public Optional<Dataset> toDomainEntity(String... params) {
		return taxonId == null || schemaId == null ? Optional.empty() :
				Optional.of(new Dataset(UUID.randomUUID(), description, taxonId, schemaId));
	}

}
