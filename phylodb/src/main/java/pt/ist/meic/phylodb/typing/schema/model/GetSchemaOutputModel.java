package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.phylogeny.locus.model.LocusOutputModel;

import java.util.Arrays;
import java.util.Objects;

/**
 * A GetSchemaOutputModel is the output model representation of an {@link Schema}
 * <p>
 * A GetSchemaOutputModel is constituted by the {@link #taxon_id}, and {@link #id} fields to identify the schema,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the schema is deprecated, and what version it has. It is also constituted
 * by the {@link #type}, which the method of this schema, by the {@link #description}, that is a description of this taxon,
 * and by the {@link #loci}, which are the set of loci that compose this schema.
 */
public class GetSchemaOutputModel extends SchemaOutputModel {

	private String type;
	private String description;
	private LocusOutputModel[] loci;

	public GetSchemaOutputModel() {
	}

	public GetSchemaOutputModel(Schema schema) {
		super(schema);
		this.type = schema.getType().getName();
		this.description = schema.getDescription();
		this.loci = schema.getLociReferences().stream()
				.map(LocusOutputModel::new)
				.toArray(LocusOutputModel[]::new);
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public LocusOutputModel[] getLoci() {
		return loci;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetSchemaOutputModel that = (GetSchemaOutputModel) o;
		return super.equals(that) &&
				Objects.equals(type, that.type) &&
				Objects.equals(description, that.description) &&
				Arrays.equals(loci, that.loci);
	}

}
