package pt.ist.meic.phylodb.typing.schema;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;

@RestController
@RequestMapping("/taxons/{taxon}/schemas")
public class SchemaController {

	private SchemaService service;

	public SchemaController(SchemaService service) {
		this.service = service;
	}

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getSchemas(@PathVariable("taxon") String taxon) {
		return null;
	}

	@GetMapping(path = "/{schema}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getSchema(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@GetMapping(path = "/{schema}/loci", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getLociBySchema(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@PutMapping(path = "/{schema}")
	public ResponseEntity putSchema(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schemaId,
			@RequestBody SchemaInputModel schema
	) {
		return null;
	}

	@DeleteMapping(path = "/{schema}")
	public ResponseEntity deleteSchema(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schemaId
	) {
		return null;
	}

}
