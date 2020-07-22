package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Objects;

/**
 * A locus is a specific location in the chromosome of an {@link Taxon taxonomic unit}
 * <p>
 * A locus is constituted by the {@link #id} field to identify the locus, the {@link #deprecated} field which indicates if the locus is deprecated, and
 * the {@link #version} field that is the version of the locus. It is also constituted by the {@link #description}, that is a description of this locus.
 */
public class Locus extends VersionedEntity<Locus.PrimaryKey> {

	private final String description;

	public Locus(String taxonId, String id, long version, boolean deprecated, String description) {
		super(new PrimaryKey(taxonId, id), version, deprecated);
		this.description = description;
	}

	public Locus(String taxonId, String id, String description) {
		this(taxonId, id, -1, false, description);
	}

	public String getTaxonId() {
		return id.getTaxonId();
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Locus locus = (Locus) o;
		return super.equals(locus) && Objects.equals(description, locus.description);
	}

	/**
	 * A Locus.PrimaryKey is the identification of a locus
	 * <p>
	 * A Locus.PrimaryKey is constituted by the {@link #taxonId} and {@link #id} fields which identify the locus.
	 */
	public static class PrimaryKey {

		private final String taxonId;
		private final String id;

		public PrimaryKey(String taxonId, String id) {
			this.taxonId = taxonId;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String getTaxonId() {
			return taxonId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(taxonId, that.taxonId) &&
					Objects.equals(id, that.id);
		}

	}

}
