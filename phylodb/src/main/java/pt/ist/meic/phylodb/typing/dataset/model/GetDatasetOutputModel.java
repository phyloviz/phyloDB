package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaOutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Objects;

public class GetDatasetOutputModel extends DatasetOutputModel {

	private String description;
	private SchemaOutputModel schema;

	public GetDatasetOutputModel() {
	}

	public GetDatasetOutputModel(Dataset dataset) {
		super(dataset);
		this.description = dataset.getDescription();
		Reference<Schema.PrimaryKey> reference = dataset.getSchema();
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
