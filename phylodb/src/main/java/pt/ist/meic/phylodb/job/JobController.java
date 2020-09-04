package pt.ist.meic.phylodb.job;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.job.model.*;
import pt.ist.meic.phylodb.security.authorization.Activity;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.utils.controller.Controller;
import pt.ist.meic.phylodb.utils.service.Pair;

import java.util.Optional;

/**
 * Class that contains the endpoints to manage jobs
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("projects/{project}/jobs")
public class JobController extends Controller {

	private JobService service;

	public JobController(JobService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Job jobs}.
	 * <p>
	 * Returns the page with information of each job. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project} that contains the jobs
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity< GetJobsOutputModel>} representing the specified job page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getJobs(
			@PathVariable("project") String projectId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getJobs(projectId, page, l), GetJobsOutputModel::new);
	}

	/**
	 * Endpoint to create the specified {@link Job job}.
	 * <p>
	 * Creates the specified job. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId  identifier of the {@link Project project} that contains the jobs
	 * @param inputModel job request
	 * @return a {@link ResponseEntity<JobAcceptedOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postJob(
			@PathVariable("project") String projectId,
			@RequestBody JobInputModel inputModel
	) {
		Optional<JobRequest> jobRequest = inputModel.toDomainEntity();
		if (!jobRequest.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST).toResponseEntity();
		Optional<Pair<String, String>> optional = service.createJob(projectId, jobRequest.get());
		return optional.isPresent() ?
				new JobAcceptedOutputModel(optional.get().getKey(), optional.get().getValue()).toResponseEntity() :
				new ErrorOutputModel(Problem.UNAUTHORIZED).toResponseEntity();
	}

	/**
	 * Endpoint to deprecate the given {@link Inference Inference}.
	 * <p>
	 * Removes the specified inference. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId identifier of the {@link Project project} that contains the jobs
	 * @param jobId     job id
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{job}")
	public ResponseEntity<?> deleteJob(
			@PathVariable("project") String projectId,
			@PathVariable("job") String jobId
	) {
		return status(() -> service.deleteJob(projectId, jobId));
	}

}
