package pt.ist.meic.phylodb.phylogeny.allele.model;

import java.util.Objects;

/**
 * A GetAlleleOutputModel is the output model representation of an {@link Allele}
 * <p>
 * A GetAlleleOutputModel is constituted by the {@link #taxon_id}, {@link #locus_id}, {@link #id}, {@link #project_id} fields to identify the allele,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the allele is deprecated, and what version it has.
 * It also contains the {@link #sequence}, that is the sequence represented by this allele.
 */
public class GetAlleleOutputModel extends AlleleOutputModel {

	private String sequence;

	public GetAlleleOutputModel() {
	}

	public GetAlleleOutputModel(Allele allele) {
		super(allele);
		this.sequence = allele.getSequence();
	}

	public String getSequence() {
		return sequence;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetAlleleOutputModel that = (GetAlleleOutputModel) o;
		return super.equals(that) && Objects.equals(sequence, that.sequence);
	}

}
