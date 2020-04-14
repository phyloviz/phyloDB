package pt.ist.meic.phylodb.typing.profile.model;

import org.apache.logging.log4j.util.Strings;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Profile extends Entity<Profile.PrimaryKey> {

	private final String aka;
	private final List<Reference<String>> allelesIds;

	public Profile(UUID projectId, UUID datasetId, String id, int version, boolean deprecated, String aka, List<Reference<String>> allelesIds) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.aka = aka;
		this.allelesIds = allelesIds;
	}

	public Profile(UUID projectId, UUID datasetId, String id, String aka, String[] allelesIds) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, aka, Arrays.stream(allelesIds)
				.map(i -> new Reference<>(i, CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public UUID getDatasetId() {
		return id.getDatasetId();
	}

	public String getAka() {
		return aka;
	}

	public List<Reference<String>> getAllelesReferences() {
		return allelesIds;
	}

	public List<String> getAllelesids() {
		return allelesIds.stream()
				.map(Entity::getPrimaryKey)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		String alleles = Strings.join(getAllelesids(), ',');
		return String.format("Profile %s from dataset %s with aka %s and alleles %s", id.getId(), id.getDatasetId(), getAka(), alleles);
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

		public UUID getProjectId() {
			return projectId;
		}

		public UUID getDatasetId() {
			return datasetId;
		}

		public String getId() {
			return id;
		}

	}

}
