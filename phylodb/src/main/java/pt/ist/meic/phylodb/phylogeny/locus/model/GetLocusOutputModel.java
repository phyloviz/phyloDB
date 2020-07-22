package pt.ist.meic.phylodb.phylogeny.locus.model;

import java.util.Objects;

/**
 * A GetLocusOutputModel is the output model representation of an {@link Locus}
 * <p>
 * A GetLocusOutputModel is constituted by the {@link #taxon_id}, and {@link #id} fields to identify the locus,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the locus is deprecated, and what version it has.
 * It also contains the {@link #description}, that is a description of this locus.
 */
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
