package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.Objects;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * An Isolate is an organism isolated from the microbial population, and is defined by a description, a set of {@link Ancillary ancillary details}
 * and can be associated to a {@link Profile profile}
 * <p>
 * An Isolate is constituted by the {@link #id} field to identify the isolate, the {@link #deprecated} field which indicates if the isolate is deprecated, and
 * the {@link #version} field that is the version of the isolate. It is also constituted by the {@link #description}, that is a description of this isolate,
 * by the {@link #ancillaries} which are a set of details associated to the isolate, and by the {@link #profile} which is the profile that this isolate is associated with.
 */
public class Isolate extends VersionedEntity<Isolate.PrimaryKey> {

	private final String description;
	private final Ancillary[] ancillaries;
	private final VersionedEntity<Profile.PrimaryKey> profile;

	public Isolate(String projectId, String datasetId, String id, long version, boolean deprecated, String description, Ancillary[] ancillaries, VersionedEntity<Profile.PrimaryKey> profile) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.description = description;
		this.profile = profile;
		this.ancillaries = ancillaries;
	}

	public Isolate(String projectId, String datasetId, String id, String description, Ancillary[] ancillaries, String profileId) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, description, ancillaries,
				profileId == null ? null : new VersionedEntity<>(new Profile.PrimaryKey(projectId, datasetId, profileId), CURRENT_VERSION_VALUE, false));
	}

	public String getDatasetId() {
		return id.getDatasetId();
	}

	public String getDescription() {
		return description;
	}

	public VersionedEntity<Profile.PrimaryKey> getProfile() {
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

	/**
	 * An Isolate.PrimaryKey is the identification of an isolate
	 * <p>
	 * An Isolate.PrimaryKey is constituted by the {@link #projectId}, {@link #datasetId}, {@link #id}, {@link #projectId}, fields which identify the isolate.
	 */
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
