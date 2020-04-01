package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Isolate extends Entity {

	private UUID datasetId;
	private String id;
	private String description;
	private Ancillary[] ancillaries;
	private Reference<String> profile;

	public Isolate(UUID datasetId, String id, int version, boolean deprecated, String description, Ancillary[] ancillaries, Reference<String> profile) {
		super(version, deprecated);
		this.datasetId = datasetId;
		this.id = id;
		this.description = description;
		this.profile = profile;
		this.ancillaries = ancillaries;
	}

	public Isolate(UUID datasetId, String id, String description, Ancillary[] ancillaries, String profileId) {
		this(datasetId, id, CURRENT_VERSION_VALUE, false, description, ancillaries, new Reference<>(profileId, CURRENT_VERSION_VALUE, false));
	}

	public Isolate(String id, String description, Ancillary[] ancillaries, String profileId) {
		this(null, id, description, ancillaries, profileId);
	}

	public UUID getDatasetId() {
		return datasetId;
	}

	public String getId() {
		return id;
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

	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(datasetId, id);
	}

	public static class PrimaryKey {

		private UUID datasetId;
		private String id;

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
