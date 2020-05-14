package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.analysis.inference.model.GetAnalysesOutputModel;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationOutputModel;
import pt.ist.meic.phylodb.analysis.visualization.model.GetVisualizationsOutputModel;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.util.UUID;

@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/analyses/{analysis}/visualizations")
public class VisualizationController extends Controller<Visualization> {

	private VisualizationService service;

	public VisualizationController(VisualizationService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getVisualizations(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("analysis") UUID analysisId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getVisualizations(projectId, datasetId, analysisId, page, l), GetVisualizationsOutputModel::new, null);
	}

	@GetMapping(path = "/{visualization}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getVisualization(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("analysis") UUID analysisId,
			@PathVariable("visualization") UUID visualizationId
	) {
		return get(() -> service.getVisualization(projectId, datasetId, analysisId, visualizationId),
				GetVisualizationOutputModel::new,
				() -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@DeleteMapping(path = "/{visualization}")
	public ResponseEntity deleteVisualization(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("analysis") UUID analysisId,
			@PathVariable("visualization") UUID visualizationId
	) {
		return status(() -> service.deleteVisualization(projectId, datasetId, analysisId, visualizationId));
	}

}
