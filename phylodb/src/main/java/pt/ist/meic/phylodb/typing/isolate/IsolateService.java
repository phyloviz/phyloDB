package pt.ist.meic.phylodb.typing.isolate;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

@Service
public class IsolateService {

	@Value("${application.missing}")
	private String missing;

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
		if(isolate == null)
			return false;
		Isolate.PrimaryKey key = isolate.getPrimaryKey();
		if (!datasetRepository.exists(new Dataset.PrimaryKey(key.getProjectId(), key.getDatasetId())) || (isolate.getProfile() != null &&
				!profileRepository.exists(isolate.getProfile().getPrimaryKey())))
			return false;
		Ancillary[] ancillaries = Arrays.stream(isolate.getAncillaries())
				.filter(a -> !a.getValue().matches(String.format("[\\s%s]*", missing)))
				.toArray(Ancillary[]::new);
		Isolate save = new Isolate(key.getProjectId(), key.getDatasetId(), key.getId(), isolate.getVersion(), isolate.isDeprecated(), isolate.getDescription(), ancillaries, isolate.getProfile());
		return isolateRepository.save(save);
	}

	@Transactional
	public boolean deleteIsolate(UUID projectId, UUID datasetId, String isolateId) {
		return isolateRepository.remove(new Isolate.PrimaryKey(projectId, datasetId, isolateId));
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveIsolatesOnConflictSkip(UUID projectId, UUID datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, false, file);
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveIsolatesOnConflictUpdate(UUID projectId, UUID datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, true, file);
	}

	private Optional<Pair<Integer[], String[]>> saveAll(UUID projectId, UUID datasetId, int idColumn, boolean conflict, MultipartFile file) throws IOException {
		if (!datasetRepository.exists(new Dataset.PrimaryKey(projectId, datasetId)))
			return Optional.empty();
		Predicate<Isolate> canSave = conflict ? i -> true : i -> !isolateRepository.exists(i.getPrimaryKey());
		Pair<List<Isolate>, List<Integer>> parsed = new IsolatesFormatter().parse(file, projectId, datasetId, idColumn, missing);
		List<String> invalids = new ArrayList<>();
		List<Isolate> isolates = parsed.getKey(), toSave = new ArrayList<>();
		for (Isolate isolate : isolates) {
			if(canSave.test(isolate) && (isolate.getProfile() == null || profileRepository.exists(isolate.getProfile().getPrimaryKey()))) {
				toSave.add(isolate);
				continue;
			}
			invalids.add(isolate.getPrimaryKey().getId());
		}
		return isolateRepository.saveAll(toSave) ?
				Optional.of(new Pair<>(parsed.getValue().toArray(new Integer[0]), invalids.toArray(new String[0]))) :
				Optional.empty();
	}

}
