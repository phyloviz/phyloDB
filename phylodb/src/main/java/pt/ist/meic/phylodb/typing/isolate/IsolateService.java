package pt.ist.meic.phylodb.typing.isolate;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Class that contains operations to manage isolates
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
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

	/**
	 * Operation to retrieve the resumed information of the requested isolates
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @param limit     number of isolates to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Isolate.PrimaryKey>}, which is the resumed information of each isolate
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Isolate.PrimaryKey>>> getIsolatesEntities(String projectId, String datasetId, int page, int limit) {
		return isolateRepository.findAllEntities(page, limit, projectId, datasetId);
	}

	/**
	 * Operation to retrieve the information of the requested isolates
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @param limit     number of isolates to retrieve by page
	 * @return an {@link Optional} with a {@link List<Isolate>} which is the information of each isolate
	 */
	@Transactional(readOnly = true)
	public Optional<List<Isolate>> getIsolates(String projectId, String datasetId, int page, int limit) {
		return isolateRepository.findAll(page, limit, projectId, datasetId);
	}

	/**
	 * Operation to retrieve the requested isolate
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param isolateId identifier of the {@link Isolate isolate}
	 * @param version   version of the isolate
	 * @return an {@link Optional} of {@link Isolate}, which is the requested isolate
	 */
	@Transactional(readOnly = true)
	public Optional<Isolate> getIsolate(String projectId, String datasetId, String isolateId, Long version) {
		return isolateRepository.find(new Isolate.PrimaryKey(projectId, datasetId, isolateId), version);
	}

	/**
	 * Operation to save an isolate
	 *
	 * @param isolate isolate to be saved
	 * @return {@code true} if the isolate was saved
	 */
	@Transactional
	public boolean saveIsolate(Isolate isolate) {
		if (isolate == null)
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

	/**
	 * Operation to deprecate an isolate
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param isolateId identifier of the {@link Isolate isolate}
	 * @return {@code true} if the isolate was deprecated
	 */
	@Transactional
	public boolean deleteIsolate(String projectId, String datasetId, String isolateId) {
		return isolateRepository.remove(new Isolate.PrimaryKey(projectId, datasetId, isolateId));
	}

	/**
	 * Operation to save several isolates if they don't exist
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param idColumn  number of the id column in the file
	 * @param file      file with the isolates
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of isolates ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<Pair<Integer[], String[]>> saveIsolatesOnConflictSkip(String projectId, String datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, false, file);
	}

	/**
	 * Operation to save several isolates
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param idColumn  number of the id column in the file
	 * @param file      file with the isolates
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of isolates ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<Pair<Integer[], String[]>> saveIsolatesOnConflictUpdate(String projectId, String datasetId, int idColumn, MultipartFile file) throws IOException {
		return saveAll(projectId, datasetId, idColumn, true, file);
	}

	private Optional<Pair<Integer[], String[]>> saveAll(String projectId, String datasetId, int idColumn, boolean conflict, MultipartFile file) throws IOException {
		if (!datasetRepository.exists(new Dataset.PrimaryKey(projectId, datasetId)))
			return Optional.empty();
		Predicate<Isolate> canSave = conflict ? i -> true : i -> !isolateRepository.exists(i.getPrimaryKey());
		Pair<List<Isolate>, List<Integer>> parsed = new IsolatesFormatter().parse(file, projectId, datasetId, idColumn, missing);
		List<String> invalids = new ArrayList<>();
		List<Isolate> isolates = parsed.getKey(), toSave = new ArrayList<>();
		for (Isolate isolate : isolates) {
			if (canSave.test(isolate) && (isolate.getProfile() == null || profileRepository.exists(isolate.getProfile().getPrimaryKey()))) {
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
