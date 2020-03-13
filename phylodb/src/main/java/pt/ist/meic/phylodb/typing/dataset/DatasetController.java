package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.mediatype.Problem;
import pt.ist.meic.phylodb.typing.dataset.model.*;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.controller.StatusOutputModel;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/datasets")
public class DatasetController extends EntityController {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDatasets(
			@RequestParam(value = "page", defaultValue = "0") Integer page
	) {
		Optional<List<Dataset>> optional = service.getDatasets(page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new GetDatasetsOutputModel(optional.get()).toResponse();
	}

	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("dataset") String datasetId
	) {
		Optional<Dataset> optional = service.getDataset(UUID.fromString(datasetId));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponse() :
				new GetDatasetOutputModel(optional.get()).toResponse();
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@RequestBody PostDatasetInputModel dataset
	) {
		StatusResult result = service.createDataset(dataset.getDescription(), dataset.getTaxonId(), dataset.getSchemaId());
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new StatusOutputModel(result.getStatus()).toResponse();
	}

	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("dataset") String datasetId,
			@RequestBody PutDatasetInputModel dataset
	) {
		StatusResult result = service.updateDataset(UUID.fromString(datasetId), new Dataset(dataset.getId(), dataset.getDescription(), null, null));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new StatusOutputModel(result.getStatus()).toResponse();
	}

	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("dataset") String datasetId
	) {
		StatusResult result = service.deleteDataset(UUID.fromString(datasetId));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponse() :
				new StatusOutputModel(result.getStatus()).toResponse();
	}

}
