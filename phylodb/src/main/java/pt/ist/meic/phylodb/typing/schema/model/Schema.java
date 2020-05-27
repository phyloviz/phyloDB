package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

public class Schema extends VersionedEntity<Schema.PrimaryKey> {

	private final Method type;
	private final String description;
	private final List<VersionedEntity<Locus.PrimaryKey>> lociIds;

	public Schema(String taxonId, String id, long version, boolean deprecated, Method type, String description, List<VersionedEntity<Locus.PrimaryKey>> lociIds) {
		super(new PrimaryKey(taxonId, id), version, deprecated);
		this.type = type;
		this.description = description;
		this.lociIds = lociIds;
	}

	public Schema(String taxonId, String id, Method type, String description, String[] lociId) {
		this(taxonId, id, -1, false, type, description, Arrays.stream(lociId)
				.map(i -> new VersionedEntity<>(new Locus.PrimaryKey(taxonId, i), CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public Method getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public List<VersionedEntity<Locus.PrimaryKey>> getLociReferences() {
		return lociIds;
	}

	public List<String> getLociIds() {
		return lociIds.stream()
				.map(l -> l.getPrimaryKey().getId())
				.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Schema schema = (Schema) o;
		return super.equals(schema) &&
				type == schema.type &&
				Objects.equals(description, schema.description) &&
				Objects.equals(lociIds, schema.lociIds);
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
