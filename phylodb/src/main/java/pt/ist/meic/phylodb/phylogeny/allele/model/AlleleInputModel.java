package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;

/**
 * An AlleleInputModel is the input model for an allele
 * <p>
 * An AlleleInputModel is constituted by the {@link #id} field to identify the allele
 * and the {@link #sequence} which is the sequence represented by this allele.
 */
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
