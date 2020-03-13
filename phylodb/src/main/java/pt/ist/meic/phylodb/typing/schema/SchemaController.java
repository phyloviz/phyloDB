package pt.ist.meic.phylodb.typing.schema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.mediatype.Problem;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemaOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemasOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.controller.StatusOutputModel;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/taxons/{taxon}/schemas")
public class SchemaController extends EntityController {

	private SchemaService service;

	public SchemaController(SchemaService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchemas(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") Integer page
	) {
		Optional<List<Schema>> optional = service.getSchemas(taxonId, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new GetSchemasOutputModel(optional.get()).toResponse();
	}

	@GetMapping(path = "/{schema}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) {
		Optional<Schema> optional = service.getSchema(taxonId, schemaId);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponse() :
				new GetSchemaOutputModel(optional.get()).toResponse();
	}

	@PutMapping(path = "/{schema}")
	public ResponseEntity<?> putSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestBody SchemaInputModel schema
	) {
		StatusResult result = service.saveSchema(taxonId, schemaId, new Schema(schema.getTaxon(), schema.getId(), schema.getDescription(), schema.getLoci()));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new StatusOutputModel(result.getStatus()).toResponse();
	}

	@DeleteMapping(path = "/{schema}")
	public ResponseEntity<?> deleteSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) {
		StatusResult result = service.deleteSchema(taxonId, schemaId);
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponse() :
				new StatusOutputModel(result.getStatus()).toResponse();
	}

}
