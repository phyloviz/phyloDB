package pt.ist.meic.phylodb.job;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.InferenceRepository;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.job.model.Job;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.profile.ProfileRepository;
import pt.ist.meic.phylodb.utils.service.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that contains operations to manage jobs
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class JobService extends pt.ist.meic.phylodb.utils.service.Service  {

	private JobRepository jobRepository;
	private ProfileRepository profileRepository;
	private InferenceRepository inferenceRepository;

	public JobService(JobRepository jobRepository, ProfileRepository profileRepository, InferenceRepository inferenceRepository) {
		this.jobRepository = jobRepository;
		this.profileRepository = profileRepository;
		this.inferenceRepository = inferenceRepository;
	}

	/**
	 * Operation to retrieve the information of the requested jobs
	 *
	 * @param projectId identifier of the {@link Project project} that contains the jobs
	 * @param page      number of the page to retrieve
	 * @param limit     number of jobs to retrieve by page
	 * @return an {@link Optional} with a {@link List<Job>}
	 */
	@Transactional(readOnly = true)
	public Optional<List<Job>> getJobs(String projectId, int page, Integer limit) {
		return jobRepository.findAll(page, limit, projectId);
	}

	/**
	 * Operation to create a job
	 * <p>
	 * It will always create a job thus also creating a job id and an analysis id.
	 * It will create the job if:
	 * - The job is an inference analysis and there is at least 2 profiles in the dataset
	 * - The job is a visualization analysis and the respective inference exists
	 *
	 * @param projectId  identifier of the {@link Project project} that contains the jobs
	 * @param jobRequest job request which contains the job information
	 * @return an {@link Optional} with a {@link Pair<String, String>} that are the created job id and the created analysis id
	 */
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

	/**
	 * Operation to remove a job
	 *
	 * @param projectId identifier of the {@link Project project} that contains the jobs
	 * @param jobId     identifier of the {@link Job job}
	 * @return {@code true} if the job was removed
	 */
	@Transactional
	public boolean deleteJob(String projectId, String jobId) {
		return jobRepository.remove(new Job.PrimaryKey(projectId, jobId));
	}

	private boolean valid(String projectId, JobRequest jobRequest) {
		Object[] params = jobRequest.getParameters();
		return jobRequest.getType() == Analysis.INFERENCE ?
				profileRepository.findAllEntities(0, 2, projectId, params[0]).orElse(Collections.emptyList()).size() > 1 :
				inferenceRepository.exists(new Inference.PrimaryKey(projectId, (String) params[0], (String) params[1]));
	}

}
