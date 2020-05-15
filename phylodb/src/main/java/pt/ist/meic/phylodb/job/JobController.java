package pt.ist.meic.phylodb.job;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.job.model.GetJobsOutputModel;
import pt.ist.meic.phylodb.job.model.JobInputModel;
import pt.ist.meic.phylodb.job.model.JobRequest;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("projects/{project}/jobs")
public class JobController extends Controller {

	private JobService service;

	public JobController(JobService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getJobs(
			@PathVariable("project") UUID projectId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getJobs(projectId, page, l), GetJobsOutputModel::new, null);
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postJob(
			@PathVariable("project") UUID projectId,
			@RequestBody JobInputModel inputModel
	) {
		Optional<JobRequest> jobRequest = inputModel.toDomainEntity();
		if(!jobRequest.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST).toResponseEntity();
		Optional<UUID> optional = service.createJob(projectId, jobRequest.get());
		return optional.isPresent() ?
				new CreatedOutputModel(optional.get()).toResponseEntity() :
				new ErrorOutputModel(Problem.UNAUTHORIZED).toResponseEntity();
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{job}")
	public ResponseEntity<?> deleteJob(
			@PathVariable("project") UUID projectId,
			@PathVariable("job") UUID jobId
	) {
		return status(() -> service.deleteJob(projectId, jobId));
	}

}
