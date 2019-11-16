package pt.ist.meic.phylodb.locus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loci")
public class LocusController {

	private LocusService service;

	public LocusController(LocusService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getLoci() {
		return null;
	}

	@GetMapping(path = "/{locus}/")
	public ResponseEntity getLocus(
			@PathVariable("locus") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{locus}/schemas")
	public ResponseEntity getSchemasByLocus(
			@PathVariable("locus") String locus
	) {
		return null;
	}
}
