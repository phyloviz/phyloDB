package pt.ist.meic.phylodb.security.authorization.project;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.authentication.user.model.User;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.authorization.project.model.GetProjectOutputModel;
import pt.ist.meic.phylodb.security.authorization.project.model.GetProjectsOutputModel;
import pt.ist.meic.phylodb.security.authorization.project.model.Project;
import pt.ist.meic.phylodb.security.authorization.project.model.ProjectInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;
import pt.ist.meic.phylodb.utils.service.Entity;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/projects")
public class ProjectController extends Controller<Project> {

	private ProjectService service;

	public ProjectController(ProjectService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProjects(
			@RequestParam(value = "page", defaultValue = "0") int page,
			HttpServletRequest req
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return getAll(type, l -> service.getProjects(new User.PrimaryKey(userId, provider), page, l), GetProjectsOutputModel::new, null);
	}

	@Authorized(role = Role.USER, permission = Operation.READ)
	@GetMapping(path = "/{project}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProject(
			@PathVariable("project") UUID projectId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
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
		return post(input::toDomainEntity, p -> service.saveProject(p, new User.PrimaryKey(userId, provider)), Entity::getPrimaryKey);
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@PutMapping(path = "/{project}")
	public ResponseEntity<?> putProject(
			@PathVariable("project") UUID projectId,
			HttpServletRequest req,
			@RequestBody ProjectInputModel input
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return put(() -> input.toDomainEntity(projectId.toString()), (p) -> service.saveProject(p, new User.PrimaryKey(userId, provider)));
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@DeleteMapping(path = "/{project}")
	public ResponseEntity<?> deleteProject(
			@PathVariable("project") String projectId
	) {
		return status(() -> service.deleteProject(UUID.fromString(projectId)));
	}

}
