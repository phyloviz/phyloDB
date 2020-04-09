package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Isolate extends Entity<Isolate.PrimaryKey> {

	private final String description;
	private final Ancillary[] ancillaries;
	private final Reference<String> profile;

	public Isolate(UUID datasetId, String id, int version, boolean deprecated, String description, Ancillary[] ancillaries, Reference<String> profile) {
		super(new PrimaryKey(datasetId, id), version, deprecated);
		this.description = description;
		this.profile = profile;
		this.ancillaries = ancillaries;
	}

	public Isolate(UUID datasetId, String id, String description, Ancillary[] ancillaries, String profileId) {
		this(datasetId, id, CURRENT_VERSION_VALUE, false, description, ancillaries,
				profileId == null ? null : new Reference<>(profileId, CURRENT_VERSION_VALUE, false));
	}

	public Isolate(String id, String description, Ancillary[] ancillaries, String profileId) {
		this(null, id, description, ancillaries, profileId);
	}

	public UUID getDatasetId() {
		return id.getDatasetId();
	}

	public String getDescription() {
		return description;
	}

	public Reference<String> getProfile() {
		return profile;
	}

	public Ancillary[] getAncillaries() {
		return ancillaries;
	}

	@Override
	public String toString() {
		String ancillaries = Arrays.stream(this.ancillaries)
				.map(Ancillary::toString)
				.collect(Collectors.joining(","));
		return String.format("Isolate %s from dataset %s with description %s and ancillary %s", id.getId(), id.getDatasetId(), getDescription(), ancillaries);
	}

	public static class PrimaryKey {

		private final UUID datasetId;
		private final String id;

		public PrimaryKey(UUID datasetId, String id) {
			this.datasetId = datasetId;
			this.id = id;
		}

		public UUID getDatasetId() {
			return datasetId;
		}

		public String getId() {
			return id;
		}

	}

}
