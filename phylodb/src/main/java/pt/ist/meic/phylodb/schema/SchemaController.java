package pt.ist.meic.phylodb.schema;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schemas")
public class SchemaController {

	private SchemaService service;

	public SchemaController(SchemaService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getSchemas() {
		return null;
	}

	@GetMapping(path = "/{schema}")
	public ResponseEntity getSchemas(
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@GetMapping(path = "/{schema}/loci")
	public ResponseEntity getLociBySchema(
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@GetMapping(path = "/{schema}/taxon/{taxon}/isolates")
	public ResponseEntity getIsolatesBySchema(
			@PathVariable("schema") String schema,
			@PathVariable("taxon") String taxon
	) {
		return null;
	}
}
