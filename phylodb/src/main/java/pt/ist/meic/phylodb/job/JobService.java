package pt.ist.meic.phylodb.job;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.inference.InferenceRepository;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobInputModel;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;

import java.util.*;

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
	public Optional<List<Job>> getJobs(UUID projectId, int page, Integer limit) {
		return jobRepository.findAll(page, limit, projectId);
	}

	@Transactional
	public Optional<Pair<UUID, UUID>> createJob(UUID projectId, JobRequest jobRequest) {
		if (!valid(projectId, jobRequest))
			return Optional.empty();
		UUID jobId = UUID.randomUUID();
		UUID analysisId = UUID.randomUUID();
		jobRepository.save(new Job(projectId, jobId, jobRequest.getType().getName() + "." + jobRequest.getAlgorithm(), analysisId, jobRequest.getParameters()));
		return Optional.of(new Pair<>(jobId, analysisId));
	}

	@Transactional
	public boolean deleteJob(UUID projectId, UUID jobId) {
		return jobRepository.remove(new Job.PrimaryKey(projectId, jobId));
	}

	private boolean valid(UUID projectId, JobRequest jobRequest) {
		UUID[] params;
		try {
			params = Arrays.stream(jobRequest.getParameters())
					.map(UUID::fromString)
					.toArray(UUID[]::new);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return params.length == JobInputModel.INFERENCE_PARAMETERS_COUNT ?
				profileRepository.findAll(0, 2, projectId, params[0]).orElse(Collections.emptyList()).size() > 1  :
				inferenceRepository.exists(new Inference.PrimaryKey(projectId, params[0], params[1]));
	}

}
