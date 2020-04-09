package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.utils.service.Entity;

public class Allele extends Entity<Allele.PrimaryKey> {

	private final String sequence;

	public Allele(String taxonId, String locusId, String id, int version, boolean deprecated, String sequence) {
		super(new PrimaryKey(taxonId, locusId, id), version, deprecated);
		this.sequence = sequence;
	}

	public Allele(String taxonId, String locusId, String id, String sequence) {
		this(taxonId, locusId, id, -1, false, sequence);
	}

	public Allele(String id, String sequence) {
		this(null, null, id, sequence);
	}

	public String getTaxonId() {
		return id.getTaxonId();
	}

	public String getLocusId() {
		return id.getLocusId();
	}

	public String getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return String.format("Allele %s from locus %s of taxon %s with sequence %s", id.getId(), id.getLocusId(), id.getTaxonId(), sequence);
	}

	public static class PrimaryKey {

		private final String taxonId;
		private final String locusId;
		private final String id;

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
