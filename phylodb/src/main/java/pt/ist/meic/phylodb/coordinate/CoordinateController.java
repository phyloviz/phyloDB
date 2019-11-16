package pt.ist.meic.phylodb.coordinate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("taxon/{taxon}/schemas/{schema}/profiles/distance/{palgorithm}/visualization/{valgorithm}")
public class CoordinateController {

	private CoordinateService service;

	public CoordinateController(CoordinateService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getVisualizationCoordinates(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("palgorithm") String pAlgorithm,
			@PathVariable("valgorithm") String vAlgorithm
	) {
		return null;
	}

	@GetMapping(path = "/status")
	public ResponseEntity getVisualizationStatus(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("palgorithm") String pAlgorithm,
			@PathVariable("valgorithm") String vAlgorithm
	) {
		return null;
	}

	@PostMapping(path = "/")
	public ResponseEntity postVisualizationCoordinates(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("palgorithm") String pAlgorithm,
			@PathVariable("valgorithm") String vAlgorithm,
			@RequestParam("file") MultipartFile file
	) {
		return null;
	}

	@PostMapping(path = "/execution")
	public ResponseEntity postVisualizationAlgorithmExecution(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("palgorithm") String pAlgorithm,
			@PathVariable("valgorithm") String vAlgorithm
	) {
		return null;
	}
}
