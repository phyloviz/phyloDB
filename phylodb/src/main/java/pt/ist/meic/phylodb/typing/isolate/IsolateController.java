package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.isolate.model.input.IsolateInputModel;
import pt.ist.meic.phylodb.typing.isolate.model.output.GetIsolateOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.output.GetIsolatesOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;
import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/datasets/{dataset}/isolates")
public class IsolateController extends EntityController {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity<?> getIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value="Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type
	) {
		if (page < 0)
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Optional<List<Isolate>> optional = service.getIsolates(datasetId, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				GetIsolatesOutputModel.get(type).apply(optional.get()).toResponseEntity();
	}

	@GetMapping(path = "/{isolate}/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		Optional<Isolate> optional = service.getIsolate(datasetId, isolateId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponseEntity() :
				new GetIsolateOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{isolate}")
	public ResponseEntity<?> putIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestBody IsolateInputModel isolateInputModel
	) {
		Optional<Isolate> optionalIsolate = isolateInputModel.toDomainEntity(datasetId.toString(), isolateId);
		if (!optionalIsolate.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveIsolate(optionalIsolate.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@DeleteMapping(path = "/{isolate}")
	public ResponseEntity<?> deleteIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId
	) {
		Status result = service.deleteIsolate(datasetId, isolateId);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PostMapping(path = "/files")
	public ResponseEntity<?> postIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody MultipartFile file
	) throws FileFormatException {
		Status result = service.saveIsolatesOnConflictSkip(datasetId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PutMapping(path = "/files")
	public ResponseEntity<?> putIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody MultipartFile file

	) throws FileFormatException {
		Status result = service.saveIsolatesOnConflictUpdate(datasetId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

}
