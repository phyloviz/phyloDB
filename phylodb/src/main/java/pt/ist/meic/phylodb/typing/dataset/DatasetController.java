package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.input.PostDatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.input.PutDatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.output.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.output.GetDatasetsOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;
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
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return page < 0 ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity() :
				new GetDatasetsOutputModel(service.getDatasets(page, Integer.parseInt(jsonLimit)).get()).toResponseEntity();
	}

	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getDataset(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		Optional<Dataset> optional = service.getDataset(datasetId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new GetDatasetOutputModel(optional.get()).toResponseEntity();
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postDataset(
			@RequestBody PostDatasetInputModel datasetInputModel
	) {
		Optional<Dataset> optionalDataset = datasetInputModel.toDomainEntity();
		if(!optionalDataset.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveDataset(optionalDataset.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result, optionalDataset.get().getId()).toResponseEntity();
	}

	@PutMapping(path = "/{dataset}")
	public ResponseEntity<?> putDataset(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody PutDatasetInputModel datasetInputModel
	) {
		Optional<Dataset> optionalDataset = datasetInputModel.toDomainEntity(datasetId.toString());
		if(!optionalDataset.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveDataset(optionalDataset.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity<?> deleteDataset(
			@PathVariable("dataset") String datasetId
	) {
		Status result = service.deleteDataset(UUID.fromString(datasetId));
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

}
