package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.typing.Method;

import java.util.Optional;

/**
 * A SchemaInputModel is the input model for a schema
 * <p>
 * A SchemaInputModel is constituted by the {@link #taxon} and {@link #id} fields to identify the schema,
 * by the{@link #type}, which the method of this schema, by the {@link #description}, that is a description of this taxon,
 * and by the {@link #loci}, which are the set of loci that compose this schema.
 */
public class SchemaInputModel implements InputModel<Schema> {

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
		return !params[0].equals(taxon) || !params[1].equals(id) || loci == null || loci.length == 0 ||
				type == null || !Method.exists(type) ?
				Optional.empty() :
				Optional.of(new Schema(taxon, id, Method.valueOf(type.toUpperCase()), description, loci));
	}

}
