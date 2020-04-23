package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLociOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLocusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Permission;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/taxons/{taxon}/loci")
public class LocusController extends Controller<Locus> {
	private LocusService service;

	public LocusController(LocusService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLoci(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getLoci(taxonId, page, l), GetLociOutputModel::new, null);
	}

	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getLocus(taxonId, locusId, version), GetLocusOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@PutMapping(path = "/{locus}")
	public ResponseEntity<?> putLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId, locusId), service::saveLocus);
	}

	@Authorized(role = Role.ADMIN, permission = Permission.WRITE)
	@DeleteMapping(path = "/{locus}")
	public ResponseEntity<?> deleteLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId
	) {
		return status(() -> service.deleteLocus(taxonId, locusId));
	}

}
