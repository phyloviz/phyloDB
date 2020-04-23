package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;
import java.util.UUID;

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
		UUID project = params[3] != null ? UUID.fromString(params[3]) : null;
		return !params[2].equals(id) ? Optional.empty() : Optional.of(new Allele(params[0], params[1], id, sequence, project));
	}

}
