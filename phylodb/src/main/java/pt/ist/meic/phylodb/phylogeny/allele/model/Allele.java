package pt.ist.meic.phylodb.phylogeny.allele.model;

import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

public class Allele extends Entity<Allele.PrimaryKey> {

	private final String sequence;

	public Allele(String taxonId, String locusId, String id, long version, boolean deprecated, String sequence, String project) {
		super(new PrimaryKey(taxonId, locusId, id, project), version, deprecated);
		this.sequence = sequence;
	}

	public Allele(String taxonId, String locusId, String id, String sequence, String project) {
		this(taxonId, locusId, id, -1, false, sequence, project);
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Allele allele = (Allele) o;
		return super.equals(allele) && Objects.equals(sequence, allele.sequence);
	}

	public static class PrimaryKey {

		private final String taxonId;
		private final String locusId;
		private final String id;
		private final String projectId;

		public PrimaryKey(String taxonId, String locusId, String id, String projectId) {
			this.taxonId = taxonId;
			this.locusId = locusId;
			this.id = id;
			this.projectId = projectId;
		}

		public PrimaryKey(String taxonId, String locusId, String id) {
			this(taxonId, locusId, id, null);
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

		public String getProjectId() {
			return projectId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(taxonId, that.taxonId) &&
					Objects.equals(locusId, that.locusId) &&
					Objects.equals(id, that.id) &&
					Objects.equals(projectId, that.projectId);
		}

	}

}
