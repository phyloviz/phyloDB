package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * A schema is a domain entity to represent a typing methodology schema, it is composed of several {@link Locus loci}
 * <p>
 * A schema is constituted by the {@link #id} field to identify the schema, the {@link #deprecated} field which indicates if the schema is deprecated, and
 * the {@link #version} field that is the version of the schema. It is also constituted by the {@link #type}, which the method of this schema,
 * by the {@link #description}, that is a description of this taxon, and by the {@link #lociIds}, which are the set of loci that compose this schema.
 */
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

	/**
	 * A Schema.PrimaryKey is the identification of a schema
	 * <p>
	 * A Schema.PrimaryKey is constituted by the {@link #taxonId}, and {@link #id} fields which identify the schema.
	 */
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
