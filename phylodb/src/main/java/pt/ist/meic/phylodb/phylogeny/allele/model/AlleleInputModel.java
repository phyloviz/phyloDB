package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.input.Input;

import java.util.Optional;

public class AlleleInputModel implements Input<Allele> {

	private String id;
	private String sequence;

	public AlleleInputModel() {
	}

	public AlleleInputModel(String id, String description) {
		this.id = id;
		this.sequence = description;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}

	@Override
	public Optional<Allele> toDomainEntity(String... params) {
		return !params[2].equals(id) ? Optional.empty() : Optional.of(new Allele(params[0], params[1], id, sequence));
	}

}
