package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLociOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLocusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage loci
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/taxons/{taxon}/loci")
public class LocusController extends Controller {

	private LocusService service;

	public LocusController(LocusService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Locus locus}.
	 * <p>
	 * Returns the page with resumed information of each locus. It requires the user to
	 * be authenticated.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param page    number of the page to retrieve
	 * @return a {@link ResponseEntity<GetLociOutputModel>} representing the specified loci page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLoci(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getLoci(taxonId, page, l), GetLociOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Locus locus}.
	 * <p>
	 * Returns all information of the specified locus. It requires the user to be authenticated.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param version version of the {@link Locus locus}
	 * @return a {@link ResponseEntity<GetLocusOutputModel>} representing the specified locus or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getLocus(taxonId, locusId, version), GetLocusOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link Locus locus}.
	 * <p>
	 * Saves a locus by parsing the input model. It requires the user to be an admin.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param input   locus input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{locus}")
	public ResponseEntity<?> putLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId, locusId), service::saveLocus);
	}

	/**
	 * Endpoint to deprecate the specified {@link Locus locus}.
	 * <p>
	 * Removes the specified locus. It requires the user to be an admin.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{locus}")
	public ResponseEntity<?> deleteLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId
	) {
		return status(() -> service.deleteLocus(taxonId, locusId));
	}

}
