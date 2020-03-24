package pt.ist.meic.phylodb.typing.schema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.typing.schema.model.output.GetSchemaOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.output.GetSchemasOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.service.StatusResult;

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
		return page < 0 ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity() :
				new GetSchemasOutputModel(service.getSchemas(taxonId, page, Integer.parseInt(jsonLimit)).get()).toResponseEntity();
	}

	@GetMapping(path = "/{schema}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) {
		Optional<Schema> optional = service.getSchema(taxonId, schemaId);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponseEntity() :
				new GetSchemaOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{schema}")
	public ResponseEntity<?> putSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestBody SchemaInputModel schemaInputModel
	) {
		Optional<Schema> schemaOptional = schemaInputModel.toDomainEntity(taxonId, schemaId);
		if(!schemaOptional.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		StatusResult result = service.saveSchema(schemaOptional.get());
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result.getStatus()).toResponseEntity();
	}

	@DeleteMapping(path = "/{schema}")
	public ResponseEntity<?> deleteSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) {
		StatusResult result = service.deleteSchema(taxonId, schemaId);
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result.getStatus()).toResponseEntity();
	}

}
