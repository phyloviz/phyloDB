package pt.ist.meic.phylodb.job;

import org.springframework.stereotype.Service;
import pt.ist.meic.phylodb.analysis.inference.InferenceRepository;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobInputModel;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.security.authorization.project.ProjectRepository;
import pt.ist.meic.phylodb.typing.dataset.DatasetRepository;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

	private JobRepository jobRepository;
	private ProjectRepository projectRepository;
	private DatasetRepository datasetRepository;
	private InferenceRepository inferenceRepository;

	public Optional<List<Job>> getJobs(UUID projectId, int page, Integer limit) {
		return jobRepository.findAll(page, limit, projectId);
	}

	public Optional<UUID> createJob(UUID projectId, JobRequest jobRequest) {
		if (!valid(projectId, jobRequest))
			return Optional.empty();
		UUID id = UUID.randomUUID();
		jobRepository.save(new Job(projectId, id, jobRequest.getType().getName() + "." + jobRequest.getAlgorithm(), jobRequest.getParameters()));
		return Optional.of(id);
	}

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
				datasetRepository.exists(new Dataset.PrimaryKey(projectId, params[0])) :
				inferenceRepository.exists(new Inference.PrimaryKey(projectId, params[0], params[1]));
	}

}
