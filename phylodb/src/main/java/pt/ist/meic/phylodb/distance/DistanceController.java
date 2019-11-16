package pt.ist.meic.phylodb.distance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("taxon/{taxon}/schemas/{schema}/profiles/distance/{algorithm}")
public class DistanceController {

	private DistanceService service;

	public DistanceController(DistanceService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getDistances(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("algorithm") String algorithm
	) {
		return null;
	}

	@GetMapping(path = "/status")
	public ResponseEntity getDistanceAlgorithmStatus(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("algorithm") String algorithm
	) {
		return null;
	}

	@PostMapping(path = "/")
	public ResponseEntity postDistances(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("algorithm") String algorithm,
			@RequestParam("file") MultipartFile file
	) {
		return null;
	}

	@PostMapping(path = "/execution")
	public ResponseEntity postDistanceAlgorithmExecution(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile,
			@PathVariable("algorithm") String algorithm
	) {
		return null;
	}
}
