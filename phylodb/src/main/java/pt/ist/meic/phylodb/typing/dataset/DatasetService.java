package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.SchemaRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@Service
public class DatasetService {

	private DatasetRepository datasetRepository;
	private SchemaRepository schemaRepository;

	public DatasetService(DatasetRepository datasetRepository, SchemaRepository schemaRepository) {
		this.datasetRepository = datasetRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Dataset>> getDatasets(int page, int limit) {
		return Optional.ofNullable(datasetRepository.findAll(page, limit));
	}

	@Transactional(readOnly = true)
	public Optional<Dataset> getDataset(UUID id) {
		return Optional.ofNullable(datasetRepository.find(id));
	}

	@Transactional
	public StatusResult createDataset(String description, String taxonId, String schemaId) {
		if (schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId)) == null)
			return new StatusResult(UNCHANGED);
		UUID id = UUID.randomUUID();
		return new StatusResult(datasetRepository.save(new Dataset(id, description, taxonId, schemaId)), id);
	}

	@Transactional
	public StatusResult updateDataset(UUID id, Dataset dataset) {
		if (!dataset.getId().equals(id) || !getDataset(id).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(datasetRepository.save(dataset));
	}

	@Transactional
	public StatusResult deleteDataset(UUID id) {
		if (!getDataset(id).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(datasetRepository.remove(id));
	}

}
