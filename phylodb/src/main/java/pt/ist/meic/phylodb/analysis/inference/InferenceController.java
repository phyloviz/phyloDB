package pt.ist.meic.phylodb.analysis.inference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.analysis.inference.model.GetInferenceOutputModel;
import pt.ist.meic.phylodb.analysis.inference.model.GetInferencesOutputModel;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Activity;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.Optional;

/**
 * Class that contains the endpoints to manage inferences
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/inferences")
public class InferenceController extends Controller {

	private InferenceService service;

	public InferenceController(InferenceService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Inference inferences}.
	 * <p>
	 * Returns the page of resumed information of each inference. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset containing the inferences
	 * @param datasetId identifier of the {@link Dataset dataset} which contains the inferences
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<GetInferencesOutputModel>} representing the specified inference page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getInferences(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getInferences(projectId, datasetId, page, l), GetInferencesOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Inference inference}.
	 * <p>
	 * Returns all information of the specified inference. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId   identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId   identifier of the {@link Dataset dataset} which contains the inference
	 * @param inferenceId identifier of the {@link Inference inference}
	 * @param format      format in which the inference should be formatted({@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEWICK} or {@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEXUS}), the default is {@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEWICK}
	 * @return a {@link ResponseEntity<GetInferenceOutputModel>} representing the specified inference or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{inference}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getInference(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId,
			@RequestParam(value = "format", defaultValue = TreeFormatter.NEWICK) String format
	) {
		if (!format.equals(TreeFormatter.NEWICK) && !format.equals(TreeFormatter.NEXUS))
			return new ErrorOutputModel(Problem.BAD_REQUEST).toResponseEntity();
		return get(() -> service.getInference(projectId, datasetId, inferenceId),
				a -> new GetInferenceOutputModel(a, format),
				() -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to create the given {@link Inference inference}.
	 * <p>
	 * Creates an inference by parsing the inference tree received in the file parameter. The algorithm parameter must be the algorithm
	 * that was run to obtain the tree given in the file. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId identifier of the {@link Project project} that contains the dataset in which it will be created the inference
	 * @param datasetId identifier of the {@link Dataset dataset} which will contain the inference
	 * @param algorithm algorithm that was run to obtain the inference(<code>goeburst<code/>)
	 * @param format    format in which the inference should be formatted({@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEWICK} or {@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEXUS}), the default is {@value pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter#NEWICK}
	 * @param file      file with the inference tree obtained by running the specified algorithm and formatted in the specified format
	 * @return a {@link ResponseEntity<CreatedOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
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

	/**
	 * Endpoint to deprecate the given {@link Inference Inference}.
	 * <p>
	 * Removes the specified inference. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId   identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId   identifier of the {@link Dataset dataset} which contains the inference
	 * @param inferenceId identifier of the {@link Inference inference}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
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
