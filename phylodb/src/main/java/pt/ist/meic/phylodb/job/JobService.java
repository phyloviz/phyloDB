package pt.ist.meic.phylodb.job;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.InferenceRepository;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

	private JobRepository jobRepository;
	private ProfileRepository profileRepository;
	private InferenceRepository inferenceRepository;

	public JobService(JobRepository jobRepository, ProfileRepository profileRepository, InferenceRepository inferenceRepository) {
		this.jobRepository = jobRepository;
		this.profileRepository = profileRepository;
		this.inferenceRepository = inferenceRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Job>> getJobs(String projectId, int page, Integer limit) {
		return jobRepository.findAll(page, limit, projectId);
	}

	@Transactional
	public Optional<Pair<String, String>> createJob(String projectId, JobRequest jobRequest) {
		if (!valid(projectId, jobRequest))
			return Optional.empty();
		String jobId = UUID.randomUUID().toString();
		String analysisId = UUID.randomUUID().toString();
		return jobRepository.save(new Job(projectId, jobId, jobRequest.getType().getName() + "." + jobRequest.getAlgorithm(), analysisId, jobRequest.getParameters())) ?
				Optional.of(new Pair<>(jobId, analysisId)) :
				Optional.empty();
	}

	@Transactional
	public boolean deleteJob(String projectId, String jobId) {
		return jobRepository.remove(new Job.PrimaryKey(projectId, jobId));
	}

	private boolean valid(String projectId, JobRequest jobRequest) {
		Object[] params = jobRequest.getParameters();
		return jobRequest.getType() == Analysis.INFERENCE ?
				profileRepository.findAllEntities(0, 2, projectId, params[0]).orElse(Collections.emptyList()).size() > 1  :
				inferenceRepository.exists(new Inference.PrimaryKey(projectId, (String) params[0], (String) params[1]));
	}

}
