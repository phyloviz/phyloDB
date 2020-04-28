package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Schema extends Entity<Schema.PrimaryKey> {

	private final Method type;
	private final String description;
	private final List<Entity<String>> lociIds;

	public Schema(String taxonId, String id, long version, boolean deprecated, Method type, String description, List<Entity<String>> lociIds) {
		super(new PrimaryKey(taxonId, id), version, deprecated);
		this.type = type;
		this.description = description;
		this.lociIds = lociIds;
	}

	public Schema(String taxonId, String id, Method type, String description, String[] lociId) {
		this(taxonId, id, -1, false, type, description, Arrays.stream(lociId)
				.map(i -> new Entity<>(i, CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public Method getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public List<Entity<String>> getLociIds() {
		return lociIds;
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
