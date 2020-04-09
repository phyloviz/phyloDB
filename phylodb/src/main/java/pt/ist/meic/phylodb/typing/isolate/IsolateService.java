package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.BatchRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
	public Optional<List<Isolate>> getIsolates(UUID datasetId, int page, int limit) {
		return isolateRepository.findAll(page, limit, datasetId);
	}

	@Transactional(readOnly = true)
	public Optional<Isolate> getIsolate(UUID datasetId, String isolateId, int version) {
		return isolateRepository.find(new Isolate.PrimaryKey(datasetId, isolateId), version);
	}

	@Transactional
	public boolean saveIsolate(Isolate isolate) {
		if (!datasetRepository.exists(isolate.getDatasetId()) || isolate.getProfile() != null &&
				!profileRepository.exists(new Profile.PrimaryKey(isolate.getDatasetId(), isolate.getProfile().getPrimaryKey())))
			return false;
		return isolateRepository.save(isolate);
	}

	@Transactional
	public boolean deleteIsolate(UUID datasetId, String isolateId) {
		return isolateRepository.remove(new Isolate.PrimaryKey(datasetId, isolateId));
	}

	@Transactional
	public boolean saveIsolatesOnConflictSkip(UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(datasetId, BatchRepository.SKIP, file);
	}

	@Transactional
	public boolean saveIsolatesOnConflictUpdate(UUID datasetId, MultipartFile file) throws IOException {
		return saveAll(datasetId, BatchRepository.UPDATE, file);
	}

	private boolean saveAll(UUID datasetId, String conflict, MultipartFile file) throws IOException {
		if (!datasetRepository.exists(datasetId))
			return false;
		List<Isolate> isolates = new IsolatesFormatter().parse(file).stream()
				.map(i -> new Isolate(datasetId, i.getPrimaryKey().getId(), null, i.getAncillaries(), i.getProfile().getPrimaryKey()))
				.collect(Collectors.toList());
		return isolateRepository.saveAll(isolates, conflict, datasetId.toString());
	}

}
