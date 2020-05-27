package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationOutputModel;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationsOutputModel;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.security.authorization.Activity;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/inferences/{inference}/visualizations")
public class VisualizationController extends Controller {

	private VisualizationService service;

	public VisualizationController(VisualizationService service) {
		this.service = service;
	}

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
