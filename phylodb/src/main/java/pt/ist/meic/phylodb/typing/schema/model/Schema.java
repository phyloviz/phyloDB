package pt.ist.meic.phylodb.typing.schema.model;

public class Schema {

	private String taxonId;
	private String id;
	private String description;
	private String[] lociIds;

	public Schema() {
	}

	public Schema(String taxonId, String id, String description, String[] lociId) {
		this.taxonId = taxonId;
		this.id = id;
		this.description = description;
		this.lociIds = lociId;
	}

	public String getTaxonId() {
		return taxonId;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String[] getLociIds() {
		return lociIds;
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
