package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetsOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage datasets
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("projects/{project}/datasets")
public class DatasetController extends Controller {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Dataset datasets}
	 * <p>
	 * Returns the page with resumed information of each dataset. It requires the user to
	 * be authenticated, and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<GetDatasetsOutputModel>} representing the specified dataset page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDatasets(
			@PathVariable("project") String projectId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getDatasets(projectId, page, l), GetDatasetsOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Dataset dataset}
	 * <p>
	 * Returns all information of the specified dataset. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param version   version of the {@link Dataset dataset}
	 * @return a {@link ResponseEntity<GetDatasetOutputModel>} representing the specified dataset or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getDataset(projectId, datasetId, version), GetDatasetOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to create the given {@link Dataset dataset}
	 * <p>
	 * Creates a dataset by parsing the input model. It requires the user to be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param input     dataset input model
	 * @return a {@link ResponseEntity<CreatedOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@PathVariable("project") String projectId,
			@RequestBody DatasetInputModel input
	) {
		return post(() -> input.toDomainEntity(projectId), service::saveDataset, d -> d.getPrimaryKey().getId());
	}

	/**
	 * Endpoint to update the given {@link Dataset dataset}
	 * <p>
	 * Updates a dataset by parsing the input model. It requires the user to be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param input     dataset input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestBody DatasetInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId, datasetId), service::saveDataset);
	}

	/**
	 * Endpoint to deprecate the specified {@link Dataset dataset}
	 * <p>
	 * Removes the specified dataset. It requires the user to be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId
	) {
		return status(() -> service.deleteDataset(projectId, datasetId));
	}

}
