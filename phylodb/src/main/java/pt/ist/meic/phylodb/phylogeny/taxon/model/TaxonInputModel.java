package pt.ist.meic.phylodb.phylogeny.taxon.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;

public class TaxonInputModel implements InputModel<Taxon> {

	private final String id;
	private final String description;

	public TaxonInputModel(String id, String description) {
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
	public Optional<Taxon> toDomainEntity(String... params) {
		return !params[0].equals(id) ? Optional.empty() : Optional.of(new Taxon(id, description));
	}

}
