package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.isolate.model.GetIsolateOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.GetIsolatesOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.isolate.model.IsolateInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage isolates
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/isolates")
public class IsolateController extends Controller {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Isolate isolates}
	 * <p>
	 * Returns the page with resumed information of each isolate. It requires the user to
	 * be authenticated, and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<GetIsolatesOutputModel>} representing the specified isolates page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getIsolates(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getIsolatesEntities(projectId, datasetId, page, l), GetIsolatesOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Isolate isolate}
	 * <p>
	 * Returns all information of the specified isolate. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param isolateId identifier of the {@link Isolate isolate}
	 * @param version   version of the {@link Isolate isolate}
	 * @return a {@link ResponseEntity<GetIsolateOutputModel>} representing the specified isolate or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{isolate}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getIsolate(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getIsolate(projectId, datasetId, isolateId, version), GetIsolateOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link Isolate isolate}.
	 * <p>
	 * Saves an isolate by parsing the input model. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param isolateId identifier of the {@link Isolate isolate}
	 * @param input     isolate input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{isolate}")
	public ResponseEntity<?> putIsolate(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestBody IsolateInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId, datasetId, isolateId), service::saveIsolate);
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Isolate isolates} in a formatted string.
	 * <p>
	 * Returns the page in formatted string. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<FileOutputModel>} representing the specified isolates page in a formatted string or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/files", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> getIsolatesFile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllFile(l -> service.getIsolates(projectId, datasetId, page, l), i -> new FileOutputModel(new IsolatesFormatter().format(i)));
	}

	/**
	 * Endpoint to create several {@link Isolate isolates}.
	 * <p>
	 * Create the isolates represented in the file if they don't exist. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param id        number of the id column in the file
	 * @param file      file with the isolates
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "/files")
	public ResponseEntity<?> postIsolates(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "id", defaultValue = "0") int id,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		return fileStatus(() -> service.saveIsolatesOnConflictSkip(projectId, datasetId, id, file));
	}

	/**
	 * Endpoint to save several {@link Isolate isolates}.
	 * <p>
	 * Saves the isolates represented in the file. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param id        number of the id column in the file
	 * @param file      file with the isolates
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/files")
	public ResponseEntity<?> putIsolates(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "id", defaultValue = "0") int id,
			@RequestParam("file") MultipartFile file

	) throws IOException {
		return fileStatus(() -> service.saveIsolatesOnConflictUpdate(projectId, datasetId, id, file));
	}

	/**
	 * Endpoint to deprecate the specified {@link Isolate isolate}.
	 * <p>
	 * Removes the specified allele. It requires the user to be an admin
	 * or if a project id is passed, to have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param isolateId identifier of the {@link Isolate isolate}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{isolate}")
	public ResponseEntity<?> deleteIsolate(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("isolate") String isolateId
	) {
		return status(() -> service.deleteIsolate(projectId, datasetId, isolateId));
	}

}
