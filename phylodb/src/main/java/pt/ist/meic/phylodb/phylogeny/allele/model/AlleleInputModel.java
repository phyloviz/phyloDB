package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;

public class AlleleInputModel implements InputModel<Allele> {

	private String id;
	private String sequence;

	public AlleleInputModel() {
	}

	public AlleleInputModel(String id, String sequence) {
		this.id = id;
		this.sequence = sequence;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}

	@Override
	public Optional<Allele> toDomainEntity(String... params) {
		String project = params[3];
		return !params[2].equals(id) ? Optional.empty() : Optional.of(new Allele(params[0], params[1], id, sequence, project));
	}

}
