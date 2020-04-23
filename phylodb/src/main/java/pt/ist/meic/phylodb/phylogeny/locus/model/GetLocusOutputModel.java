package pt.ist.meic.phylodb.phylogeny.locus.model;

import java.util.Objects;

public class GetLocusOutputModel extends LocusOutputModel {

	private String description;

	public GetLocusOutputModel() {
	}

	public GetLocusOutputModel(Locus locus) {
		super(locus);
		this.description = locus.getDescription();
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetLocusOutputModel that = (GetLocusOutputModel) o;
		return super.equals(that) && Objects.equals(description, that.description);
	}

}
