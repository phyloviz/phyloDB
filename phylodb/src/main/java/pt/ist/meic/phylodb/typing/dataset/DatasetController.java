package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Permission;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetsOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("projects/{project}/datasets")
public class DatasetController extends Controller<Dataset> {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, permission = Permission.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDatasets(
			@PathVariable("project") UUID projectId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getDatasets(projectId, page, l), GetDatasetsOutputModel::new, null);
	}

	@Authorized(role = Role.USER, permission = Permission.READ)
	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getDataset(projectId, datasetId, version), GetDatasetOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.USER, permission = Permission.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@PathVariable("project") UUID projectId,
			@RequestBody DatasetInputModel input
	) {
		return post(() -> input.toDomainEntity(projectId.toString()), service::saveDataset, d -> d.getPrimaryKey().getId());
	}

	@Authorized(role = Role.USER, permission = Permission.WRITE)
	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@RequestBody DatasetInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId.toString(), datasetId.toString()), service::saveDataset);
	}

	@Authorized(role = Role.USER, permission = Permission.WRITE)
	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId
	) {
		return status(() -> service.deleteDataset(projectId, datasetId));
	}

}
