package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationOutputModel;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationsOutputModel;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Activity;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.utils.controller.Controller;

/**
 * Class that contains the endpoints to manage visualizations
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/inferences/{inference}/visualizations")
public class VisualizationController extends Controller {

	private VisualizationService service;

	public VisualizationController(VisualizationService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link pt.ist.meic.phylodb.analysis.visualization.model.Visualization visualizations}.
	 * <p>
	 * Returns the page of resumed information of each visualization. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId   identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId   identifier of the {@link Dataset dataset} which contains the inference
	 * @param inferenceId identifier of the {@link Inference Inference}
	 * @param page        number of the page to retrieve
	 * @return a {@link ResponseEntity<GetVisualizationsOutputModel>} representing the specified inference page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getVisualizations(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getVisualizations(projectId, datasetId, inferenceId, page, l), GetVisualizationsOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link pt.ist.meic.phylodb.analysis.visualization.model.Visualization visualization}.
	 * <p>
	 * Returns all information of the specified visualization. It requires the user to
	 * be authenticated and have access to the project.
	 *
	 * @param projectId       identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId       identifier of the {@link Dataset dataset} which contains the inference
	 * @param inferenceId     identifier of the {@link Inference inference}
	 * @param visualizationId identifier of the  {@link pt.ist.meic.phylodb.analysis.visualization.model.Visualization visualization}
	 * @return a {@link ResponseEntity<GetVisualizationOutputModel>} representing the specified visualization or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{visualization}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getVisualization(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId,
			@PathVariable("visualization") String visualizationId
	) {
		return get(() -> service.getVisualization(projectId, datasetId, inferenceId, visualizationId),
				GetVisualizationOutputModel::new,
				() -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to deprecate an visualization
	 * <p>
	 * Removes the specified visualization. It requires the user to be authenticated and have access to this project.
	 *
	 * @param projectId       identifier of the {@link Project project} that contains the dataset containing the inference
	 * @param datasetId       identifier of the {@link Dataset dataset} which contains the inference
	 * @param inferenceId     identifier of the {@link Inference inference}
	 * @param visualizationId identifier of the  {@link pt.ist.meic.phylodb.analysis.visualization.model.Visualization visualization}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(activity = Activity.ALGORITHMS, role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{visualization}")
	public ResponseEntity<?> deleteVisualization(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("inference") String inferenceId,
			@PathVariable("visualization") String visualizationId
	) {
		return status(() -> service.deleteVisualization(projectId, datasetId, inferenceId, visualizationId));
	}

}
