package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.MultipleOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/datasets")
public class DatasetController extends Controller<Dataset> {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	@Authorized
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDatasets(
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getDatasets(page, l), MultipleOutputModel::new, null);
	}

	@Authorized
	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		return get(() -> service.getDataset(datasetId, version), DatasetOutputModel::new, () -> new ErrorOutputModel(Problem.UNAUTHORIZED));
	}

	@Authorized
	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@RequestBody DatasetInputModel input
	) {
		return post(input::toDomainEntity, d -> service.saveDataset(d));
	}

	@Authorized
	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody DatasetInputModel input
	) {
		return put(() -> input.toDomainEntity(datasetId.toString()), service::saveDataset);
	}

	@Authorized
	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("dataset") String datasetId
	) throws IOException {
		return status(() -> service.deleteDataset(UUID.fromString(datasetId)));
	}

}
