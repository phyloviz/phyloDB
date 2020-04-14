package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.MultipleOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Permission;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

@RestController
@RequestMapping("/taxons")
public class TaxonController extends Controller<Taxon> {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxons(
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getTaxons(page, l), MultipleOutputModel::new, null);
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "version", defaultValue = "-1") int version
	) {
		return get(() -> service.getTaxon(taxonId, version), TaxonOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@PutMapping(path = "/{taxon}")
	public ResponseEntity<?> saveTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId), service::saveTaxon);
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity<?> deleteTaxon(
			@PathVariable("taxon") String taxonId
	) {
		return status(() -> service.deleteTaxon(taxonId));
	}

}
