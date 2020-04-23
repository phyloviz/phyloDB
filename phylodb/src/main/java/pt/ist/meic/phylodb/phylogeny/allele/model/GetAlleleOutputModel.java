package pt.ist.meic.phylodb.phylogeny.allele.model;

import java.util.Objects;

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
