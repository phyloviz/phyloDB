package pt.ist.meic.phylodb.phylogeny.allele.model;

public class Allele {

	private String taxonId;
	private String locusId;
	private String id;
	private String sequence;

	public Allele() {
	}

	public Allele(String id, String sequence) {
		this.id = id;
		this.sequence = sequence;
	}

	public Allele(String taxonId, String locusId, String id, String sequence) {
		this.taxonId = taxonId;
		this.locusId = locusId;
		this.id = id;
		this.sequence = sequence;
	}

	public String getTaxonId() {
		return taxonId;
	}

	public String getLocusId() {
		return locusId;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}


	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(taxonId, locusId, id);
	}

	public static class PrimaryKey {

		private String taxonId;
		private String locusId;
		private String id;

		public PrimaryKey(String taxonId, String locusId, String id) {
			this.taxonId = taxonId;
			this.locusId = locusId;
			this.id = id;
		}

		public String getTaxonId() {
			return taxonId;
		}

		public String getLocusId() {
			return locusId;
		}

		public String getId() {
			return id;
		}

	}

}
