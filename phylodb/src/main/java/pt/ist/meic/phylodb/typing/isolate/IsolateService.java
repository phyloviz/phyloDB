package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.Status.*;

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
		return Optional.ofNullable(isolateRepository.findAll(page, limit, datasetId));
	}

	@Transactional(readOnly = true)
	public Optional<Isolate> getIsolate(UUID datasetId, String isolateId, int version) {
		return Optional.ofNullable(isolateRepository.find(new Isolate.PrimaryKey(datasetId, isolateId), version));
	}

	@Transactional
	public Status saveIsolate(Isolate isolate) {
		if(!datasetRepository.exists(isolate.getDatasetId()) || isolate.getProfile() != null &&
				!profileRepository.exists(new Profile.PrimaryKey(isolate.getDatasetId(), isolate.getProfile().getId())))
			return UNCHANGED;
		return isolateRepository.save(isolate);
	}

	@Transactional
	public Status deleteIsolate(UUID datasetId, String isolateId) {
		return isolateRepository.remove(new Isolate.PrimaryKey(datasetId, isolateId));
	}

	@Transactional
	public Status saveIsolatesOnConflictSkip(UUID datasetId, MultipartFile file) throws FileFormatException {
		if (!datasetRepository.exists(datasetId))
			return UNCHANGED;
		isolateRepository.saveAllOnConflictSkip(datasetId, new IsolatesFormatter().read(file).getEntities());
		return CREATED;
	}

	@Transactional
	public Status saveIsolatesOnConflictUpdate(UUID datasetId, MultipartFile file) throws FileFormatException {
		if (!datasetRepository.exists(datasetId))
			return UNCHANGED;
		isolateRepository.saveAllOnConflictUpdate(datasetId, new IsolatesFormatter().read(file).getEntities());
		return UPDATED;
	}

}
