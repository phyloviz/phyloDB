package pt.ist.meic.phylodb.analysis.inference;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.analysis.inference.model.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AnalysisService {

	@Value("${application.missing}")
	private String missing;

	private DatasetRepository datasetRepository;
	private ProfileRepository profileRepository;
	private AnalysisRepository analysisRepository;

	public AnalysisService(DatasetRepository datasetRepository, ProfileRepository profileRepository, AnalysisRepository analysisRepository) {
		this.datasetRepository = datasetRepository;
		this.profileRepository = profileRepository;
		this.analysisRepository = analysisRepository;
	}

	public Optional<List<Analysis>> getAnalyses(UUID projectId, UUID datasetId, int page, int limit) {
		return analysisRepository.findAll(page, limit, projectId, datasetId);
	}

	public Optional<Analysis> getAnalysis(UUID projectId, UUID datasetId, UUID id, String algorithm) {

		return InferenceAlgorithm.exists(algorithm) ?
				analysisRepository.find(new Analysis.PrimaryKey(projectId, datasetId, id, InferenceAlgorithm.valueOf(algorithm)), EntityRepository.CURRENT_VERSION_VALUE) :
				Optional.empty();
	}

	public Optional<UUID> saveAnalysis(UUID projectId, UUID datasetId, String algorithm, String format, MultipartFile file) throws IOException {
		TreeFormatter formatter;
		if(!InferenceAlgorithm.exists(algorithm) || (formatter = TreeFormatter.get(format)) == null ||
				!datasetRepository.exists(new Dataset.PrimaryKey(projectId, datasetId)))
			return Optional.empty();
		Pair<List<Edge>, List<Integer>> parsed = formatter.parse(file, projectId, datasetId, missing);
		if(parsed.getValue().size() > 0)
			return Optional.empty();
		List<Edge> edges = parsed.getKey();
		List<Entity<Profile.PrimaryKey>> profiles = edges.stream()
				.flatMap(e -> Stream.of(e.getFrom(), e.getTo()))
				.distinct()
				.collect(Collectors.toList());
		if(profileRepository.anyMissing(profiles))
			return Optional.empty();
		UUID id = UUID.randomUUID();
		analysisRepository.save(new Analysis(projectId, datasetId, id, InferenceAlgorithm.valueOf(algorithm), edges));
		return Optional.of(id);
	}

	public boolean deleteAnalysis(UUID projectId, UUID datasetId, UUID id, String algorithm) {
		return InferenceAlgorithm.exists(algorithm) && analysisRepository.remove(new Analysis.PrimaryKey(projectId, datasetId, id, InferenceAlgorithm.valueOf(algorithm)));
	}

}
