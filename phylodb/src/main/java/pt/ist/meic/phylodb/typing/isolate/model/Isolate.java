package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Isolate extends Entity<Isolate.PrimaryKey> {

	private final String description;
	private final Ancillary[] ancillaries;
	private final Entity<Profile.PrimaryKey> profile;

	public Isolate(UUID projectId, UUID datasetId, String id, long version, boolean deprecated, String description, Ancillary[] ancillaries, Entity<Profile.PrimaryKey>  profile) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.description = description;
		this.profile = profile;
		this.ancillaries = ancillaries;
	}

	public Isolate(UUID projectId,UUID datasetId, String id, String description, Ancillary[] ancillaries, String profileId) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, description, ancillaries,
				profileId == null ? null : new Entity<>(new Profile.PrimaryKey(projectId, datasetId, profileId), CURRENT_VERSION_VALUE, false));
	}

	public UUID getDatasetId() {
		return id.getDatasetId();
	}

	public String getDescription() {
		return description;
	}

	public Entity<Profile.PrimaryKey> getProfile() {
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

		private final UUID projectId;
		private final UUID datasetId;
		private final String id;

		public PrimaryKey(UUID projectId, UUID datasetId, String id) {
			this.projectId = projectId;
			this.datasetId = datasetId;
			this.id = id;
		}

		public UUID getDatasetId() {
			return datasetId;
		}
		public UUID getProjectId() {
			return projectId;
		}

		public String getId() {
			return id;
		}

	}

}
