package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonsOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

@RestController
@RequestMapping("/taxons")
public class TaxonController extends Controller {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxons(
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getTaxons(page, l), GetTaxonsOutputModel::new);
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "version", defaultValue = "-1") Long version
	) {
		return get(() -> service.getTaxon(taxonId, version), GetTaxonOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@PutMapping(path = "/{taxon}")
	public ResponseEntity<?> saveTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId), service::saveTaxon);
	}

	@Authorized(role = Role.ADMIN, operation = Operation.WRITE)
	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity<?> deleteTaxon(
			@PathVariable("taxon") String taxonId
	) {
		return status(() -> service.deleteTaxon(taxonId));
	}

}
