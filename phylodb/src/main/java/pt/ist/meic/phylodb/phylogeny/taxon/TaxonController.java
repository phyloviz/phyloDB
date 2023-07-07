package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxaOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

/**
 * Class that contains the endpoints to manage taxa
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/taxa")
public class TaxonController extends Controller {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Taxon taxa}.
	 * <p>
	 * Returns the page with resumed information of each taxon. It requires the user to
	 * be authenticated.
	 *
	 * @param page number of the page to retrieve
	 * @return a {@link ResponseEntity<GetTaxaOutputModel>} representing the specified taxa page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxa(
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getTaxa(page, l), GetTaxaOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Taxon taxon}.
	 * <p>
	 * Returns all information of the specified taxon. It requires the user to be authenticated.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param version version of the {@link Taxon taxon}
	 * @return a {@link ResponseEntity<GetTaxonOutputModel>} representing the specified taxon or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "version", defaultValue = "-1") Long version
	) {
		return get(() -> service.getTaxon(taxonId, version), GetTaxonOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link Taxon taxon}.
	 * <p>
	 * Saves a taxon by parsing the input model. It requires the user to be an admin.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param input   taxon input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{taxon}")
	public ResponseEntity<?> saveTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId), service::saveTaxon);
	}

	/**
	 * Endpoint to deprecate the specified {@link Taxon taxon}.
	 * <p>
	 * Removes the specified taxon. It requires the user to be an admin.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity<?> deleteTaxon(
			@PathVariable("taxon") String taxonId
	) {
		return status(() -> service.deleteTaxon(taxonId));
	}

}
