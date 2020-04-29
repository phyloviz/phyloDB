package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;
import java.util.UUID;

public class DatasetInputModel implements InputModel<Dataset> {

	private UUID id;
	private String description;
	private String taxonId;
	private String schemaId;

	public DatasetInputModel() {
	}

	public DatasetInputModel(UUID id, String description, String taxonId, String schemaId) {
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
		UUID id = params.length == 1 ? UUID.randomUUID() : UUID.fromString(params[1]);
		return (params.length != 1 && !params[1].equals(this.id.toString())) || taxonId == null || schemaId == null ? Optional.empty() :
				Optional.of(new Dataset(UUID.fromString(params[0]), id, schemaId, description, taxonId));
	}

}
