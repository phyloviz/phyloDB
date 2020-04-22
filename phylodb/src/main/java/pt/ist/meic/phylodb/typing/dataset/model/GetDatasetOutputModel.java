package pt.ist.meic.phylodb.typing.dataset.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Reference;

public class DatasetOutputModel extends SingleOutputModel<Dataset.PrimaryKey> {

	private final String description;
	private final String taxon_id;
	private final String schema_id;
	private final Long schema_version;
	private final boolean schema_deprecated;

	public DatasetOutputModel(Dataset dataset) {
		super(dataset);
		this.description = dataset.getDescription();
		Reference<Schema.PrimaryKey> schema = dataset.getSchema();
		this.taxon_id = schema.getPrimaryKey().getTaxonId();
		this.schema_id = schema.getPrimaryKey().getId();
		this.schema_version = schema.getVersion();
		this.schema_deprecated = schema.isDeprecated();
	}

	public String getDescription() {
		return description;
	}

	public String getTaxon_id() {
		return taxon_id;
	}

	public String getSchema_id() {
		return schema_id;
	}

	public Long getSchema_version() {
		return schema_version;
	}

	public boolean isSchema_deprecated() {
		return schema_deprecated;
	}

}
