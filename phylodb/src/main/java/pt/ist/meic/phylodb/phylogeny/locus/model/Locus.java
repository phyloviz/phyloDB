package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.utils.service.Entity;

public class Locus extends Entity<Locus.PrimaryKey> {

	private final String description;

	public Locus(String taxonId, String id, int version, boolean deprecated, String description) {
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

	}

}
