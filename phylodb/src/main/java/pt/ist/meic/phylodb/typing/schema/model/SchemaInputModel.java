package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.input.Input;

import java.util.Optional;

public class SchemaInputModel implements Input<Schema> {

	private String taxon;
	private String id;
	private String type;
	private String description;
	private String[] loci;

	public SchemaInputModel() {
	}

	public SchemaInputModel(String taxon, String id, String type, String description, String[] loci) {
		this.taxon = taxon;
		this.id = id;
		this.type = type;
		this.description = description;
		this.loci = loci;
	}

	public String getTaxon() {
		return taxon;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String[] getLoci() {
		return loci;
	}

	@Override
	public Optional<Schema> toDomainEntity(String... params) {
		return !params[0].equals(id) || taxon == null || type == null || loci == null ? Optional.empty() :
				Optional.of(new Schema(taxon, id, type, description, loci));
	}

}
