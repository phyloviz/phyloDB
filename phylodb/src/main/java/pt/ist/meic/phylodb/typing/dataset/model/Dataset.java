package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class Dataset extends VersionedEntity<Dataset.PrimaryKey> {

	private final String description;
	private final VersionedEntity<Schema.PrimaryKey> schema;

	public Dataset(String projectId, String id, long version, boolean deprecated, String description, VersionedEntity<Schema.PrimaryKey> schema) {
		super(new PrimaryKey(projectId, id), version, deprecated);
		this.description = description;
		this.schema = schema;
	}

	public Dataset(String projectId, String id, String description, String taxonId, String schemaId) {
		this(projectId, id, CURRENT_VERSION_VALUE, false, description, new VersionedEntity<>(new Schema.PrimaryKey(taxonId, schemaId), CURRENT_VERSION_VALUE, false));
	}

	public String getDescription() {
		return description;
	}

	public VersionedEntity<Schema.PrimaryKey> getSchema() {
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

		private final String projectId;
		private final String id;

		public PrimaryKey(String projectId, String id) {
			this.projectId = projectId;
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getId() {
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
