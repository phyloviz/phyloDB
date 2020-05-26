package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetsOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("projects/{project}/datasets")
public class DatasetController extends Controller {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDatasets(
			@PathVariable("project") String projectId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getDatasets(projectId, page, l), GetDatasetsOutputModel::new, null);
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getDataset(projectId, datasetId, version), GetDatasetOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@PathVariable("project") String projectId,
			@RequestBody DatasetInputModel input
	) {
		return post(() -> input.toDomainEntity(projectId), service::saveDataset, d -> d.getPrimaryKey().getId());
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestBody DatasetInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId, datasetId), service::saveDataset);
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId
	) {
		return status(() -> service.deleteDataset(projectId, datasetId));
	}

}
