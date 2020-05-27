package pt.ist.meic.phylodb.security.project;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.GetProjectOutputModel;
import pt.ist.meic.phylodb.security.project.model.GetProjectsOutputModel;
import pt.ist.meic.phylodb.security.project.model.ProjectInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import javax.servlet.http.HttpServletRequest;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/projects")
public class ProjectController extends Controller {

	private ProjectService service;

	public ProjectController(ProjectService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProjects(
			@RequestParam(value = "page", defaultValue = "0") int page,
			HttpServletRequest req
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return getAllJson(l -> service.getProjects(new User.PrimaryKey(userId, provider), page, l), GetProjectsOutputModel::new);
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{project}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProject(
			@PathVariable("project") String projectId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) long version
	) {
		return get(() -> service.getProject(projectId, version), GetProjectOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postProject(
			@RequestBody ProjectInputModel input,
			HttpServletRequest req
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return post(input::toDomainEntity, p -> service.saveProject(p, new User.PrimaryKey(userId, provider)), VersionedEntity::getPrimaryKey);
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{project}")
	public ResponseEntity<?> putProject(
			@PathVariable("project") String projectId,
			HttpServletRequest req,
			@RequestBody ProjectInputModel input
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return put(() -> input.toDomainEntity(projectId.toString()), (p) -> service.saveProject(p, new User.PrimaryKey(userId, provider)));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{project}")
	public ResponseEntity<?> deleteProject(
			@PathVariable("project") String projectId
	) {
		return status(() -> service.deleteProject(projectId));
	}

}
