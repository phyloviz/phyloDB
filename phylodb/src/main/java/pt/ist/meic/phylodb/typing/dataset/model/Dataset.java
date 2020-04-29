package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Dataset extends Entity<Dataset.PrimaryKey> {

	private final String description;
	private final Entity<Schema.PrimaryKey> schema;

	public Dataset(UUID projectId, UUID id, long version, boolean deprecated, String description, Entity<Schema.PrimaryKey> schema) {
		super(new PrimaryKey(projectId, id), version, deprecated);
		this.description = description;
		this.schema = schema;
	}

	public Dataset(UUID projectId, UUID id, String description, String taxonId, String schemaId) {
		this(projectId, id, CURRENT_VERSION_VALUE, false, description, new Entity<>(new Schema.PrimaryKey(taxonId, schemaId), CURRENT_VERSION_VALUE, false));
	}

	public String getDescription() {
		return description;
	}

	public Entity<Schema.PrimaryKey> getSchema() {
		return schema;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Dataset dataset = (Dataset) o;
		return super.equals(dataset) &&
				Objects.equals(description, dataset.description) &&
				Objects.equals(schema, dataset.schema);
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(projectId, that.projectId) &&
					Objects.equals(id, that.id);
		}

	}

}
