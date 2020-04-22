package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class SchemaOutputModel extends SingleOutputModel<Schema.PrimaryKey> {

	private final String type;
	private final String description;
	private final Object[] loci;

	public SchemaOutputModel(Schema schema) {
		super(schema);
		this.type = schema.getType().getName();
		this.description = schema.getDescription();
		this.loci = schema.getLociIds().toArray();
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public Object[] getLoci() {
		return loci;
	}

}
