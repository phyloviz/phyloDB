package pt.ist.meic.phylodb.job;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

	private JobService service;

	public JobController(JobService service) {
		this.service = service;
	}

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getJobs() {
		return null;
	}

	@GetMapping(path = "/{job}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getJob(
			@PathVariable("job") String job
	) {
		return null;
	}

	@PostMapping(path = "")
	public ResponseEntity postJob(
			@RequestBody JobInputModel job
	) {
		return null;
	}

	@DeleteMapping(path = "/{job}")
	public ResponseEntity deleteJob(
			@PathVariable("job") String schemaId
	) {
		return null;
	}
}
