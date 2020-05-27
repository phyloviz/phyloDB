package pt.ist.meic.phylodb.analysis.inference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.analysis.inference.model.GetInferenceOutputModel;
import pt.ist.meic.phylodb.analysis.inference.model.GetInferencesOutputModel;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.security.authorization.Activity;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/inferences")
public class InferenceController extends Controller {

	private InferenceService service;

	public InferenceController(InferenceService service) {
		this.service = service;
	}

	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getInferences(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getInferences(projectId, datasetId, page, l), GetInferencesOutputModel::new);
	}

	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{inference}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getInference(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId,
			@RequestParam(value = "format", defaultValue = TreeFormatter.NEWICK) String format
	) {
		if(!format.equals(TreeFormatter.NEWICK) && !format.equals(TreeFormatter.NEXUS))
			return new ErrorOutputModel(Problem.BAD_REQUEST).toResponseEntity();
		return get(() -> service.getInference(projectId, datasetId, inferenceId),
				a -> new GetInferenceOutputModel(a, format),
				() -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "")
	public ResponseEntity<?> postInference(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam("algorithm") String algorithm,
			@RequestParam("format") String format,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		Optional<String> optional = service.saveInference(projectId, datasetId, algorithm, format, file);
		return optional.isPresent() ?
				new CreatedOutputModel(optional.get()).toResponseEntity() :
				new ErrorOutputModel(Problem.UNAUTHORIZED).toResponseEntity();
	}

	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{inference}")
	public ResponseEntity<?> deleteInference(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId
	) {
		return status(() -> service.deleteInference(projectId, datasetId, inferenceId));
	}

}
