package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class Locus extends Entity<Locus.PrimaryKey> {

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
