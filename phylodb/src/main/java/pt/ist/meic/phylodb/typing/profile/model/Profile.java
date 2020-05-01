package pt.ist.meic.phylodb.typing.profile.model;

import org.apache.logging.log4j.util.Strings;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Profile extends Entity<Profile.PrimaryKey> {

	private final String aka;
	private final List<Entity<Allele.PrimaryKey>> allelesIds;

	public Profile(UUID projectId, UUID datasetId, String id, long version, boolean deprecated, String aka, List<Entity<Allele.PrimaryKey>> allelesIds) {
		super(new PrimaryKey(projectId, datasetId, id), version, deprecated);
		this.aka = aka;
		this.allelesIds = allelesIds;
	}

	public Profile(UUID projectId, UUID datasetId, String id, String aka, String[] allelesIds) {
		this(projectId, datasetId, id, CURRENT_VERSION_VALUE, false, aka, Arrays.stream(allelesIds)
				.map(i -> new Entity<>(new Allele.PrimaryKey(null, null, i), CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public UUID getDatasetId() {
		return id.getDatasetId();
	}

	public String getAka() {
		return aka;
	}

	public List<Entity<Allele.PrimaryKey>> getAllelesReferences() {
		return allelesIds;
	}

	public List<String> getAllelesIds() {
		return allelesIds.stream()
				.map(e -> e.getPrimaryKey().getId())
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		String alleles = Strings.join(getAllelesIds(), ',');
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
