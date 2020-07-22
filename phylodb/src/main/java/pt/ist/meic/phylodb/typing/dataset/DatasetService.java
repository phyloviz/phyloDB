package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.VersionedRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage datasets
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
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

	/**
	 * Operation to retrieve the information of the requested projects that the user has access to
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param page      number of the page to retrieve
	 * @param limit     number of datasets to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Dataset.PrimaryKey>}, which is the resumed information of each dataset
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Dataset.PrimaryKey>>> getDatasets(String projectId, int page, int limit) {
		return datasetRepository.findAllEntities(page, limit, projectId);
	}

	/**
	 * Operation to retrieve the requested dataset
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param id        identifier of the {@link Dataset dataset}
	 * @param version   version of the dataset
	 * @return an {@link Optional} of {@link Dataset}, which is the requested dataset
	 */
	@Transactional(readOnly = true)
	public Optional<Dataset> getDataset(String projectId, String id, long version) {
		return datasetRepository.find(new Dataset.PrimaryKey(projectId, id), version);
	}

	/**
	 * Operation to save a project
	 * <p>
	 * It will save the given dataset if the specified schema exists, and
	 * if the dataset already exists,  with any {@link Profile profile}, then the schema cannot be different of what it has already.
	 *
	 * @param dataset {@link Dataset dataset} to be saved
	 * @return {@code true} if the dataset was saved
	 */
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

	/**
	 * Operation to deprecate a dataset
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param id        identifier of the {@link Dataset dataset}
	 * @return {@code true} if the dataset was deprecated
	 */
	@Transactional
	public boolean deleteDataset(String projectId, String id) {
		return datasetRepository.remove(new Dataset.PrimaryKey(projectId, id));
	}

}
