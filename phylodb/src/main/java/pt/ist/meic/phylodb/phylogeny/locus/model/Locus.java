package pt.ist.meic.phylodb.phylogeny.locus.model;

public class Locus {

	private String taxonId;
	private String id;
	private String description;

	public Locus() {
	}

	public Locus(String taxonId, String _id, String description) {
		this.taxonId = taxonId;
		this.id = _id;
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
