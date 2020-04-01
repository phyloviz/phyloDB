package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.Status.CREATED;
import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

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
	public Optional<List<Dataset>> getDatasets(int page, int limit) {
		return Optional.ofNullable(datasetRepository.findAll(page, limit));
	}

	@Transactional(readOnly = true)
	public Optional<Dataset> getDataset(UUID id, int version) {
		return Optional.ofNullable(datasetRepository.find(id, version));
	}

	@Transactional
	public Status saveDataset(Dataset dataset) {
		Schema.PrimaryKey schemaKey = dataset.getSchema().getId();
		if(!schemaRepository.exists(schemaKey))
			return UNCHANGED;
		Dataset dbDataset = datasetRepository.find(dataset.getId(), EntityRepository.CURRENT_VERSION_VALUE);
		if (dbDataset == null) {
			datasetRepository.save(dataset);
			return CREATED;
		} else if(!schemaKey.equals(dbDataset.getSchema().getId()) &&
				profileRepository.findAll(0, 1, dataset.getId()).size() > 0)
			return UNCHANGED;
		return datasetRepository.save(dataset);
	}

	@Transactional
	public Status deleteDataset(UUID id) {
		return datasetRepository.remove(id);
	}

}
