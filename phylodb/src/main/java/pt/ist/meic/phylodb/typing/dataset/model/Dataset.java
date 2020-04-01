package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Dataset extends Entity {

	private UUID id;
	private String description;
	private Reference<Schema.PrimaryKey> schema;


	public Dataset(UUID id, int version, boolean deprecated, String description, Reference<Schema.PrimaryKey> schema) {
		super(version, deprecated);
		this.id = id;
		this.schema = schema;
		this.description = description;
	}

	public Dataset(UUID id, String taxonId, String schemaId, String description) {
		this(id, CURRENT_VERSION_VALUE, false, description, new Reference<>(new Schema.PrimaryKey(taxonId, schemaId), CURRENT_VERSION_VALUE, false));
	}

	public Dataset(String description, String taxonId, String schemaId) {
		this(UUID.randomUUID(), description, taxonId, schemaId);
	}


	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Reference<Schema.PrimaryKey> getSchema() {
		return schema;
	}

}
