package pt.ist.meic.phylodb.typing.dataset.model.input;

import pt.ist.meic.phylodb.input.Input;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

import java.util.Optional;
import java.util.UUID;

public class PutDatasetInputModel implements Input<Dataset> {

	private UUID id;
	private String description;
	private String taxonId;
	private String schemaId;

	public PutDatasetInputModel() {
	}

	public PutDatasetInputModel(UUID id, String description, String taxonId, String schemaId) {
		this.id = id;
		this.description = description;
		this.taxonId = taxonId;
		this.schemaId = schemaId;
	}

	public UUID getId() {
		return id;
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
		return !params[0].equals(id.toString()) || taxonId == null || schemaId == null ? Optional.empty() :
				Optional.of(new Dataset(UUID.fromString(params[0]), description, taxonId, schemaId));
	}

}
