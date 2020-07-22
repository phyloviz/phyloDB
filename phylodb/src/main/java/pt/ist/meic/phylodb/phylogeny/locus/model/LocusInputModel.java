package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;

/**
 * An LocusInputModel is the input model for a locus
 * <p>
 * An LocusInputModel is constituted by the {@link #id} field to identify the locus
 * and the {@link #description} which is a description of this locus.
 */
public class LocusInputModel implements InputModel<Locus> {

	private String id;
	private String description;

	public LocusInputModel() {
	}

	public LocusInputModel(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Optional<Locus> toDomainEntity(String... params) {
		return !params[1].equals(id) ? Optional.empty() : Optional.of(new Locus(params[0], id, description));
	}

}
