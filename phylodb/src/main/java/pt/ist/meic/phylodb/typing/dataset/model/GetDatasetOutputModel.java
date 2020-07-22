package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * A GetDatasetOutputModel is the output model representation of a {@link Dataset}
 * <p>
 * A GetDatasetOutputModel is constituted by the {@link #project_id}, and {@link #id} fields which identify the dataset,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the dataset is deprecated, and what version it has.
 * It also contains the the {@link #description}, that is the dataset description and the {@link #schema}, which is
 * {@link SchemaOutputModel schema} it follows.
 */
public class GetDatasetOutputModel extends DatasetOutputModel {

	private String description;
	private SchemaOutputModel schema;

	public GetDatasetOutputModel() {
	}

	public GetDatasetOutputModel(Dataset dataset) {
		super(dataset);
		this.description = dataset.getDescription();
		VersionedEntity<Schema.PrimaryKey> reference = dataset.getSchema();
		this.schema = new SchemaOutputModel(reference);
	}

	public String getDescription() {
		return description;
	}

	public SchemaOutputModel getSchema() {
		return schema;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetDatasetOutputModel that = (GetDatasetOutputModel) o;
		return super.equals(that) &&
				Objects.equals(description, that.description) &&
				Objects.equals(schema, that.schema);
	}

}
