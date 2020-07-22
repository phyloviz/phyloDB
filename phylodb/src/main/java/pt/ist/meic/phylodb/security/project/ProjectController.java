package pt.ist.meic.phylodb.security.project;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.SecurityInterceptor;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.GetProjectOutputModel;
import pt.ist.meic.phylodb.security.project.model.GetProjectsOutputModel;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.security.project.model.ProjectInputModel;
import pt.ist.meic.phylodb.security.user.model.User;
import pt.ist.meic.phylodb.utils.controller.Controller;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import javax.servlet.http.HttpServletRequest;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage projects
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController extends Controller {

	private ProjectService service;

	public ProjectController(ProjectService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Project projects}
	 * <p>
	 * Returns the page with resumed information of each project. It requires the user to
	 * be authenticated, and will only retrieve the projects which the user has access to.
	 *
	 * @param page number of the page to retrieve
	 * @param req  current HTTP request
	 * @return a {@link ResponseEntity<GetProjectsOutputModel>} representing the specified project page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProjects(
			@RequestParam(value = "page", defaultValue = "0") int page,
			HttpServletRequest req
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return getAllJson(l -> service.getProjects(new User.PrimaryKey(userId, provider), page, l), GetProjectsOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Project project}
	 * <p>
	 * Returns all information of the specified project. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param version   version of the {@link Project project}
	 * @return a {@link ResponseEntity<GetProjectOutputModel>} representing the specified project or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{project}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProject(
			@PathVariable("project") String projectId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) long version
	) {
		return get(() -> service.getProject(projectId, version), GetProjectOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to create the given {@link Project project}
	 * <p>
	 * Creates a project by parsing the input model. It requires the user to be authenticated and have access to this project.
	 *
	 * @param input project input model
	 * @param req   current HTTP request
	 * @return a {@link ResponseEntity<CreatedOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@PostMapping(path = "")
	public ResponseEntity<?> postProject(
			@RequestBody ProjectInputModel input,
			HttpServletRequest req
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return post(input::toDomainEntity, p -> service.saveProject(p, new User.PrimaryKey(userId, provider)), VersionedEntity::getPrimaryKey);
	}

	/**
	 * Endpoint to update the given {@link Project project}
	 * <p>
	 * Updates a project by parsing the input model. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param input     project input model
	 * @param req       current HTTP request
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{project}")
	public ResponseEntity<?> putProject(
			@PathVariable("project") String projectId,
			HttpServletRequest req,
			@RequestBody ProjectInputModel input
	) {
		String userId = (String) req.getAttribute(SecurityInterceptor.ID);
		String provider = (String) req.getAttribute(SecurityInterceptor.PROVIDER);
		return put(() -> input.toDomainEntity(projectId), (p) -> service.saveProject(p, new User.PrimaryKey(userId, provider)));
	}

	/**
	 * Endpoint to deprecate the specified {@link Project project}
	 * <p>
	 * Removes the specified project. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{project}")
	public ResponseEntity<?> deleteProject(
			@PathVariable("project") String projectId
	) {
		return status(() -> service.deleteProject(projectId));
	}

}
