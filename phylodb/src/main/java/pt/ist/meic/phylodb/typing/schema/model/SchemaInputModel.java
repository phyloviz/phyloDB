package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.typing.Method;

import java.util.Optional;

public class SchemaInputModel implements InputModel<Schema> {

	private final String taxon;
	private final String id;
	private final String type;
	private final String description;
	private final String[] loci;

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
		return !params[0].equals(id) || taxon == null || loci == null || !Method.exists(type) ? Optional.empty() :
				Optional.of(new Schema(taxon, id, Method.valueOf(type), description, loci));
	}

}
