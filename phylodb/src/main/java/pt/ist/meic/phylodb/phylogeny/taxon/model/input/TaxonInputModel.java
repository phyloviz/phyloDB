package pt.ist.meic.phylodb.phylogeny.taxon.model.input;

import pt.ist.meic.phylodb.input.Input;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

import java.util.Optional;

public class TaxonInputModel implements Input<Taxon> {

	private String id;
	private String description;

	public TaxonInputModel() {
	}

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
