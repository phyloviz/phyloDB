package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Schema extends Entity<Schema.PrimaryKey> {

	private final Method type;
	private final String description;
	private final List<Reference<String>> lociIds;

	public Schema(String taxonId, String id, int version, boolean deprecated, Method type, String description, List<Reference<String>> lociIds) {
		super(new PrimaryKey(taxonId, id), version, deprecated);
		this.type = type;
		this.description = description;
		this.lociIds = lociIds;
	}

	public Schema(String taxonId, String id, Method type, String description, String[] lociId) {
		this(taxonId, id, -1, false, type, description, Arrays.stream(lociId)
				.map(i -> new Reference<>(i, CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public Method getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public List<Reference<String>> getLociIds() {
		return lociIds;
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

	}

}
