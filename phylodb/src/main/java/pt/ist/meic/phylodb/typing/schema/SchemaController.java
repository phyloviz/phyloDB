package pt.ist.meic.phylodb.typing.schema;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemaOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.GetSchemasOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.typing.schema.model.SchemaInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage schemas
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/taxons/{taxon}/schemas")
public class SchemaController extends Controller {

	private SchemaService service;

	public SchemaController(SchemaService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Schema schemas}.
	 * <p>
	 * Returns the page with resumed information of each schema. It requires the user to
	 * be authenticated.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param page    number of the page to retrieve
	 * @return a {@link ResponseEntity<GetSchemasOutputModel>} representing the specified schemas page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchemas(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getSchemas(taxonId, page, l), GetSchemasOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Schema schema}.
	 * <p>
	 * Returns all information of the specified schema. It requires the user to be authenticated.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param schemaId identifier of the {@link Schema schema}
	 * @param version  version of the {@link Schema schema}
	 * @return a {@link ResponseEntity<GetSchemaOutputModel>} representing the specified schema or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "/{schema}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) long version
	) {
		return get(() -> service.getSchema(taxonId, schemaId, version), GetSchemaOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link Schema schema}.
	 * <p>
	 * Saves a schema by parsing the input model. It requires the user to be an admin.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param schemaId identifier of the {@link Schema schema}
	 * @param input    schema input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{schema}")
	public ResponseEntity<?> putSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId,
			@RequestBody SchemaInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId, schemaId), service::saveSchema);
	}

	/**
	 * Endpoint to deprecate the specified {@link Schema schema}.
	 * <p>
	 * Removes the specified schema. It requires the user to be an admin.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param schemaId identifier of the {@link Schema schema}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{schema}")
	public ResponseEntity<?> deleteSchema(
			@PathVariable("taxon") String taxonId,
			@PathVariable("schema") String schemaId
	) {
		return status(() -> service.deleteSchema(taxonId, schemaId));
	}

}
