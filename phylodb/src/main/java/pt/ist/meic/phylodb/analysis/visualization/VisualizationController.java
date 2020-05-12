package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/datasets/{dataset}/analyses/{analysis}/visualizations")
public class VisualizationController {

	private VisualizationService service;

	public VisualizationController(VisualizationService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getVisualizations(
			@PathVariable("analysis") String analysis
	) {
		return null;
	}

	@GetMapping(path = "/{visualization}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getVisualization(
			@PathVariable("analysis") String analysis,
			@PathVariable("visualization") String visualization
	) {
		return null;
	}

	@DeleteMapping(path = "/{visualization}")
	public ResponseEntity deleteVisualization(
			@PathVariable("analysis") String analysis,
			@PathVariable("visualization") String visualization
	) {
		return null;
	}

}
