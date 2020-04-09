package pt.ist.meic.phylodb.typing.schema;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.MultipleOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.typing.schema.model.SchemaOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RequestMapping("/taxons/{taxon}/schemas")
public class SchemaController extends Controller<Schema> {

	private SchemaService service;

	public SchemaController(SchemaService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchemas(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getSchemas(taxonId, page, l), MultipleOutputModel::new, null);
	}

	@GetMapping(path = "/{schema}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		return get(() -> service.getSchema(taxonId, schemaId, version), SchemaOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(Role.ADMIN)
	@PutMapping(path = "/{schema}")
	public ResponseEntity<?> putSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestBody SchemaInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId, schemaId), service::saveSchema);
	}

	@Authorized(Role.ADMIN)
	@DeleteMapping(path = "/{schema}")
	public ResponseEntity<?> deleteSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) throws IOException {
		return status(() -> service.deleteSchema(taxonId, schemaId));
	}

}
