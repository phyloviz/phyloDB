package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.isolate.model.GetIsolateOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.GetIsolatesOutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.IsolateInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/isolates")
public class IsolateController extends Controller {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<?> getIsolates(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getIsolatesEntities(projectId, datasetId, page, l), GetIsolatesOutputModel::new);
	}

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

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/files", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<?> getIsolatesFile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllFile(l -> service.getIsolates(projectId, datasetId, page, l), i -> new FileOutputModel(new IsolatesFormatter().format(i)));
	}

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
