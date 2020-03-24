package pt.ist.meic.phylodb.typing.profile.model;

import java.util.UUID;

public class Profile {

	private String datasetId;
	private String id;
	private String aka;
	private String[] allelesIds;

	public Profile() {
	}

	public Profile(String datasetId, String id, String aka, String[] allelesIds) {
		this.datasetId = datasetId;
		this.id = id;
		this.aka = aka;
		this.allelesIds = allelesIds;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String getId() {
		return id;
	}

	public String getAka() {
		return aka;
	}

	public String[] getAllelesIds() {
		return allelesIds;
	}

	public static class PrimaryKey {

		private UUID datasetId;
		private String id;

		public PrimaryKey(UUID datasetId, String id) {
			this.datasetId = datasetId;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public UUID getDatasetId() {
			return datasetId;
		}

	}

}
