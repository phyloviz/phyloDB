package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class Profile extends VersionedEntity<Profile.PrimaryKey> {

	private final String aka;
	private final List<VersionedEntity<Allele.PrimaryKey>> allelesIds;

	public Profile(String projectId, String datasetId, String id, long version, boolean deprecated, String aka, List<VersionedEntity<Allele.PrimaryKey>> allelesIds) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.aka = aka;
		this.allelesIds = allelesIds;
	}

	public Profile(String projectId, String datasetId, String id, String aka, String[] allelesIds) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, aka, Arrays.stream(allelesIds)
				.map(a -> a != null ? new VersionedEntity<>(new Allele.PrimaryKey(null, null, a, null), CURRENT_VERSION_VALUE, false) : null)
				.collect(Collectors.toList()));
	}

	public String getDatasetId() {
		return id.getDatasetId();
	}

	public String getAka() {
		return aka;
	}

	public List<VersionedEntity<Allele.PrimaryKey>> getAllelesReferences() {
		return allelesIds;
	}

	public Profile updateReferences(Schema schema, String missing, boolean authorized) {
		String taxon = schema.getPrimaryKey().getTaxonId();
		List<VersionedEntity<Locus.PrimaryKey>> loci = schema.getLociReferences();
		List<VersionedEntity<Allele.PrimaryKey>> alleles = this.getAllelesReferences();
		PrimaryKey key = this.getPrimaryKey();
		BiFunction<String, String, Allele.PrimaryKey> cons = authorized ? (l, a) -> new Allele.PrimaryKey(taxon, l, a, key.getProjectId()) :
				(l, a) -> new Allele.PrimaryKey(taxon, l, a);
		return new Profile(key.getProjectId(), key.getDatasetId(), key.getId(), this.getVersion(), this.isDeprecated(), this.getAka(), IntStream.range(0, alleles.size())
				.mapToObj(i -> {
					VersionedEntity<Allele.PrimaryKey> ref = alleles.get(i);
					if (ref != null)
						return !ref.getPrimaryKey().getId().matches(String.format("[\\s%s]*", missing)) && !ref.getPrimaryKey().getId().isEmpty() ?
								new VersionedEntity<>(cons.apply(loci.get(i).getPrimaryKey().getId(), ref.getPrimaryKey().getId()), ref.getVersion(), ref.isDeprecated()) :
								null;
					return null;
				})
				.collect(Collectors.toList()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Profile profile = (Profile) o;
		return super.equals(profile) &&
				Objects.equals(aka, profile.aka) &&
				Objects.equals(allelesIds, profile.allelesIds);
	}

	public static class PrimaryKey {

		private final String projectId;
		private final String datasetId;
		private final String id;

		public PrimaryKey(String projectId, String datasetId, String id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getDatasetId() {
			return datasetId;
		}

		public String getId() {
			return id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(projectId, that.projectId) &&
					Objects.equals(datasetId, that.datasetId) &&
					Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId, datasetId, id);
		}

	}

}
