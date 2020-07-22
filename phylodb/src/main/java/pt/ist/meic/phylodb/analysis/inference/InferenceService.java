package pt.ist.meic.phylodb.analysis.inference;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that contains operations to manage inferences
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class InferenceService {

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private InferenceRepository inferenceRepository;

	public InferenceService(DatasetRepository datasetRepository, ProfileRepository profileRepository, InferenceRepository analysisRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.inferenceRepository = analysisRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested inferences
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset containing the inferences
	 * @param datasetId identifier of the {@link Dataset dataset} which contains the inferences
	 * @param page      number of the page to retrieve
	 * @param limit     number of inferences to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link Entity<Inference.PrimaryKey>}, which is the resumed information of each inference
	 */
	@Transactional(readOnly = true)
	public Optional<List<Entity<Inference.PrimaryKey>>> getInferences(String projectId, String datasetId, int page, int limit) {
		return inferenceRepository.findAllEntities(page, limit, projectId, datasetId);
	}

	/**
	 * Operation to retrieve the requested inference
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId identifier of the {@link Dataset dataset} which contains the inference
	 * @param id        identifier of the {@link Inference Inference}
	 * @return an {@link Optional} of {@link Inference}, which is the requested inference
	 */
	@Transactional(readOnly = true)
	public Optional<Inference> getInference(String projectId, String datasetId, String id) {
		return inferenceRepository.find(new Inference.PrimaryKey(projectId, datasetId, id));
	}

	/**
	 * Operation to create an inference
	 * <p>
	 * It will always create a new inference, since an inference is one computation of an inference algorithm, thus can't be changed.
	 * It will create a new inference if the specified algorithm format, and the tree represented in the file are valid, and if all profiles used in the edges
	 * exists in the database, otherwise it won't create the inference and will return an empty optional.
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId identifier of the {@link Dataset dataset} which contains the inference
	 * @param algorithm algorithm that was run to obtain the inference(<code>goeburst<code/>)
	 * @param format    format in which the inference should be formatted({@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEWICK} or {@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEXUS})
	 * @param file      file with the inference tree obtained by running the specified algorithm and formatted in the specified format
	 * @return an {@link Optional} of with an identifier of the created inference
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<String> saveInference(String projectId, String datasetId, String algorithm, String format, MultipartFile file) throws IOException {
		TreeFormatter formatter;
		if (!InferenceAlgorithm.exists(algorithm) || (formatter = TreeFormatter.get(format)) == null ||
				!datasetRepository.exists(new Dataset.PrimaryKey(projectId, datasetId)))
			return Optional.empty();
		Pair<List<Edge>, List<Integer>> parsed = formatter.parse(file, projectId, datasetId);
		if (parsed.getValue().size() > 0)
			return Optional.empty();
		List<Edge> edges = parsed.getKey();
		List<VersionedEntity<Profile.PrimaryKey>> profiles = edges.stream()
				.flatMap(e -> Stream.of(e.getFrom(), e.getTo()))
				.distinct()
				.collect(Collectors.toList());
		if (edges.size() == 0 || profileRepository.anyMissing(profiles))
			return Optional.empty();
		UUID id = UUID.randomUUID();
		inferenceRepository.save(new Inference(projectId, datasetId, id.toString(), InferenceAlgorithm.valueOf(algorithm.toUpperCase()), edges));
		return Optional.of(id.toString());
	}

	/**
	 * Operation to deprecate an inference
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId identifier of the {@link Dataset dataset} which contains the inference
	 * @param id        identifier of the {@link Inference Inference}
	 * @return {@code true} if the inference was deprecated
	 */
	@Transactional
	public boolean deleteInference(String projectId, String datasetId, String id) {
		return inferenceRepository.remove(new Inference.PrimaryKey(projectId, datasetId, id));
	}

}
