package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.utils.service.Entity;

public class Allele extends Entity {

	private String taxonId;
	private String locusId;
	private String id;
	private String sequence;

	public Allele(String taxonId, String locusId, String id, int version, boolean deprecated, String sequence) {
		super(version, deprecated);
		this.taxonId = taxonId;
		this.locusId = locusId;
		this.id = id;
		this.sequence = sequence;
	}

	public Allele(String taxonId, String locusId, String id, String sequence) {
		this(taxonId, locusId, id, -1, false, sequence);
	}

	public Allele(String id, String sequence) {
		this(null, null, id, sequence);
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
