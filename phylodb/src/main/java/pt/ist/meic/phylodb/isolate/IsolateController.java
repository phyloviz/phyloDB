package pt.ist.meic.phylodb.isolate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taxon/{taxon}/isolates")
public class IsolateController {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	// Filtered by ancillary data
	@GetMapping(path = "/")
	public ResponseEntity getIsolates(
			@PathVariable("taxon") String taxon
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/")
	public ResponseEntity getIsolate(
			@PathVariable("taxon") String taxon,
			@PathVariable("isolate") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/ancillary")
	public ResponseEntity getAncillaryByIsolate(
			@PathVariable("taxon") String taxon,
			@PathVariable("isolate") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/schemas")
	public ResponseEntity getSchemasByIsolate(
			@PathVariable("taxon") String taxon,
			@PathVariable("isolate") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/schemas/{schema}/profiles")
	public ResponseEntity getSchemasByIsolate(
			@PathVariable("taxon") String taxon,
			@PathVariable("isolate") String locus,
			@PathVariable("schema") String schema
	) {
		return null;
	}
}
