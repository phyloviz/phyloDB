package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Dataset extends Entity<Dataset.PrimaryKey> {

	private final String description;
	private final Reference<Schema.PrimaryKey> schema;

	public Dataset(UUID projectId, UUID id, long version, boolean deprecated, String description, Reference<Schema.PrimaryKey> schema) {
		super(new PrimaryKey(projectId, id), version, deprecated);
		this.description = description;
		this.schema = schema;
	}

	public Dataset(UUID projectId, UUID id, String description, String taxonId, String schemaId) {
		this(projectId, id, CURRENT_VERSION_VALUE, false, description, new Reference<>(new Schema.PrimaryKey(taxonId, schemaId), CURRENT_VERSION_VALUE, false));
	}

	public String getDescription() {
		return description;
	}

	public Reference<Schema.PrimaryKey> getSchema() {
		return schema;
	}

	public static class PrimaryKey {

		private final UUID projectId;
		private final UUID id;

		public PrimaryKey(UUID projectId, UUID id) {
			this.projectId = projectId;
			this.id = id;
		}

		public UUID getProjectId() {
			return projectId;
		}

		public UUID getId() {
			return id;
		}

	}

}
