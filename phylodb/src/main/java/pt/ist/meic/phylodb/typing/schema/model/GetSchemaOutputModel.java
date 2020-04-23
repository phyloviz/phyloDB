package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.phylogeny.locus.model.LocusOutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.Objects;

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
		this.loci = schema.getLociIds().stream()
				.map(r -> new LocusOutputModel(taxon_id, r))
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
