package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Profile extends Entity {

	private UUID datasetId;
	private String id;
	private String aka;
	private List<Reference<String>> allelesIds;

	public Profile(UUID datasetId, String id, int version, boolean deprecated, String aka, List<Reference<String>> allelesIds) {
		super(version, deprecated);
		this.datasetId = datasetId;
		this.id = id;
		this.aka = aka;
		this.allelesIds = allelesIds;
	}

	public Profile(UUID datasetId, String id, String aka, String[] allelesIds) {
		this(datasetId, id, CURRENT_VERSION_VALUE, false, aka, Arrays.stream(allelesIds)
				.map(i -> new Reference<>(i, CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}


	public UUID getDatasetId() {
		return datasetId;
	}

	public String getId() {
		return id;
	}

	public String getAka() {
		return aka;
	}

	public List<Reference<String>> getAllelesIds() {
		return allelesIds;
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

		public String getId() {
			return id;
		}

		public UUID getDatasetId() {
			return datasetId;
		}

	}

}
