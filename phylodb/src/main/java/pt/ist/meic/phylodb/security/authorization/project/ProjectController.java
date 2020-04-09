package pt.ist.meic.phylodb.security.authorization.project;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.security.authorization.project.model.ProjectInputModel;

@RestController
@RequestMapping("/projects")
public class ProjectController {

	private ProjectService service;

	public ProjectController(ProjectService service) {
		this.service = service;
	}

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getProjects(
	) {
		return null;
	}

	@GetMapping(path = "/{project}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getProject(
			@PathVariable("project") String project
	) {
		return null;
	}

	@PostMapping(path = "/")
	public ResponseEntity postProject(
			@RequestBody ProjectInputModel project
	) {
		return null;
	}

	@PutMapping(path = "/{project}")
	public ResponseEntity getProject(
			@PathVariable("project") String projectId,
			@RequestBody ProjectInputModel project
	) {
		return null;
	}

	@DeleteMapping(path = "/{project}")
	public ResponseEntity deleteProject(
			@PathVariable("project") String project
	) {
		return null;
	}

}
