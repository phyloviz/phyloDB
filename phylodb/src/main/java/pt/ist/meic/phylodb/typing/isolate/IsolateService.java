package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IsolateService {

	private DatasetRepository datasetRepository;
	private IsolateRepository isolateRepository;
	private ProfileRepository profileRepository;

	public IsolateService(DatasetRepository datasetRepository, IsolateRepository isolateRepository, ProfileRepository profileRepository) {
		this.datasetRepository = datasetRepository;
		this.isolateRepository = isolateRepository;
		this.profileRepository = profileRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Isolate>> getIsolates(UUID projectId, UUID datasetId, int page, int limit) {
		return isolateRepository.findAll(page, limit, projectId, datasetId);
	}

	@Transactional(readOnly = true)
	public Optional<Isolate> getIsolate(UUID projectId, UUID datasetId, String isolateId, Long version) {
		return isolateRepository.find(new Isolate.PrimaryKey(projectId, datasetId, isolateId), version);
	}

	@Transactional
	public boolean saveIsolate(Isolate isolate) {
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		if (!datasetRepository.exists(new Dataset.PrimaryKey(key.getProjectId(), key.getDatasetId())) || isolate.getProfile() != null &&
				!profileRepository.exists(new Profile.PrimaryKey(key.getProjectId(), isolate.getDatasetId(), isolate.getProfile().getPrimaryKey())))
			return false;
		return isolateRepository.save(isolate).isPresent();
	}

	@Transactional
	public boolean deleteIsolate(UUID projectId,UUID datasetId, String isolateId) {
		return isolateRepository.remove(new Isolate.PrimaryKey(projectId, datasetId, isolateId));
	}

	@Transactional
	public boolean saveIsolatesOnConflictSkip(UUID projectId,UUID datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, BatchRepository.SKIP, file);
	}

	@Transactional
	public boolean saveIsolatesOnConflictUpdate(UUID projectId, UUID datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, BatchRepository.UPDATE, file);
	}

	private boolean saveAll(UUID projectId,UUID datasetId, int idColumn, String conflict, MultipartFile file) throws IOException {
		if (!datasetRepository.exists(new Dataset.PrimaryKey(projectId, datasetId)))
			return false;
		List<Isolate> isolates = new IsolatesFormatter().parse(file, projectId, datasetId, idColumn);
		return isolateRepository.saveAll(isolates, conflict, datasetId.toString()).isPresent();
	}

}
