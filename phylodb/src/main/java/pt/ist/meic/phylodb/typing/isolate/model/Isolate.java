package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Isolate extends Entity<Isolate.PrimaryKey> {

	private final String description;
	private final Ancillary[] ancillaries;
	private final Entity<Profile.PrimaryKey> profile;

	public Isolate(String projectId, String datasetId, String id, long version, boolean deprecated, String description, Ancillary[] ancillaries, Entity<Profile.PrimaryKey> profile) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.description = description;
		this.profile = profile;
		this.ancillaries = ancillaries;
	}

	public Isolate(String projectId, String datasetId, String id, String description, Ancillary[] ancillaries, String profileId) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, description, ancillaries,
				profileId == null ? null : new Entity<>(new Profile.PrimaryKey(projectId, datasetId, profileId), CURRENT_VERSION_VALUE, false));
	}

	public String getDatasetId() {
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Isolate isolate = (Isolate) o;
		return super.equals(isolate) &&
				Objects.equals(description, isolate.description) &&
				Arrays.equals(ancillaries, isolate.ancillaries) &&
				Objects.equals(profile, isolate.profile);
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

		public String getDatasetId() {
			return datasetId;
		}

		public String getProjectId() {
			return projectId;
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

	}

}
