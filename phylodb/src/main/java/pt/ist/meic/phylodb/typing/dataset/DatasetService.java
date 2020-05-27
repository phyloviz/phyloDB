package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {

	private DatasetRepository datasetRepository;
	private SchemaRepository schemaRepository;
	private ProfileRepository profileRepository;

	public DatasetService(DatasetRepository datasetRepository, SchemaRepository schemaRepository, ProfileRepository profileRepository) {
		this.datasetRepository = datasetRepository;
		this.schemaRepository = schemaRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Dataset.PrimaryKey>>> getDatasets(String projectId, int page, int limit) {
		return datasetRepository.findAllEntities(page, limit, projectId);
	}

	@Transactional(readOnly = true)
	public Optional<Dataset> getDataset(String projectId, String id, long version) {
		return datasetRepository.find(new Dataset.PrimaryKey(projectId, id), version);
	}

	@Transactional
	public boolean saveDataset(Dataset dataset) {
		if (dataset == null)
			return false;
		Schema.PrimaryKey schemaKey = dataset.getSchema().getPrimaryKey();
		if (!schemaRepository.exists(schemaKey))
			return false;
		Optional<Dataset> dbDataset = datasetRepository.find(dataset.getPrimaryKey(), VersionedRepository.CURRENT_VERSION_VALUE);
		if (dbDataset.isPresent()) {
			Optional<List<VersionedEntity<Profile.PrimaryKey>>> profiles = profileRepository.findAllEntities(0, 1, dataset.getPrimaryKey().getProjectId(), dataset.getPrimaryKey().getId());
			if (!schemaKey.equals(dbDataset.get().getSchema().getPrimaryKey()) && profiles.isPresent() && profiles.get().size() > 0)
				return false;
		}
		return datasetRepository.save(dataset);
	}

	@Transactional
	public boolean deleteDataset(String projectId, String id) {
		return datasetRepository.remove(new Dataset.PrimaryKey(projectId, id));
	}

}
