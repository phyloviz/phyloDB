package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;
import java.util.UUID;

public class DatasetInputModel implements InputModel<Dataset> {

	private final UUID id;
	private final String description;
	private final String taxonId;
	private final String schemaId;

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
		UUID id = params.length == 0 ? UUID.randomUUID() : UUID.fromString(params[0]);
		return (params.length != 0 && !params[0].equals(this.id.toString())) || taxonId == null || schemaId == null ? Optional.empty() :
				Optional.of(new Dataset(id, schemaId, description, taxonId));
	}

}
