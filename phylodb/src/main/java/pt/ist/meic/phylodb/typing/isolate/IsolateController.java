package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.MultipleOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.typing.isolate.model.IsolateInputModel;
import pt.ist.meic.phylodb.typing.isolate.model.IsolateOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/datasets/{dataset}/isolates")
public class IsolateController extends Controller<Isolate> {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	@Authorized
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity<?> getIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type
	) {
		return getAll(type, l -> service.getIsolates(datasetId, page, l),
				MultipleOutputModel::new,
				(i) -> new FileOutputModel("isolates.txt", new IsolatesFormatter().format(i)));
	}

	@Authorized
	@GetMapping(path = "/{isolate}/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		return get(() -> service.getIsolate(datasetId, isolateId, version), IsolateOutputModel::new, () -> new ErrorOutputModel(Problem.UNAUTHORIZED));
	}

	@Authorized
	@PutMapping(path = "/{isolate}")
	public ResponseEntity<?> putIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId,
			@RequestBody IsolateInputModel input
	) {
		return put(() -> input.toDomainEntity(datasetId.toString(), isolateId), service::saveIsolate);
	}

	@Authorized
	@DeleteMapping(path = "/{isolate}")
	public ResponseEntity<?> deleteIsolate(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("isolate") String isolateId
	) throws IOException {
		return status(() -> service.deleteIsolate(datasetId, isolateId));
	}

	@Authorized
	@PostMapping(path = "/files")
	public ResponseEntity<?> postIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody MultipartFile file
	) throws IOException {
		return status(() -> service.saveIsolatesOnConflictSkip(datasetId, file));
	}

	@Authorized
	@PutMapping(path = "/files")
	public ResponseEntity<?> putIsolates(
			@PathVariable("dataset") UUID datasetId,
			@RequestBody MultipartFile file

	) throws IOException {
		return status(() -> service.saveIsolatesOnConflictUpdate(datasetId, file));
	}

}
