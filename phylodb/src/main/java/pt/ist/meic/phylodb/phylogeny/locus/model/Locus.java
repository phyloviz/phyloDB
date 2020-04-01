package pt.ist.meic.phylodb.phylogeny.locus.model;

import pt.ist.meic.phylodb.utils.service.Entity;

public class Locus extends Entity {

	private String taxonId;
	private String id;
	private String description;

	public Locus(String taxonId, String id, int version, boolean deprecated, String description) {
		super(version, deprecated);
		this.taxonId = taxonId;
		this.id = id;
		this.description = description;
	}

	public Locus(String taxonId, String id, String description) {
		super(-1, false);
		this.taxonId = taxonId;
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getTaxonId() {
		return taxonId;
	}

	public String getDescription() {
		return description;
	}

	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(taxonId, id);
	}

	public static class PrimaryKey {

		private String taxonId;
		private String id;

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
