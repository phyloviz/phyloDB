package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.mediatype.Problem;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLociOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLocusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.controller.PutOutputModel;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/taxons/{taxon}/loci")
public class LocusController extends EntityController {
	private LocusService service;

	public LocusController(LocusService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLoci(
			@PathVariable("taxon") String taxon,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		Optional<List<Locus>> optional = service.getLoci(taxon, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new GetLociOutputModel(optional.get()).toResponse();
	}

	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		Optional<Locus> optional = service.getLocus(taxon, locus);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponse():
				new GetLocusOutputModel(optional.get()).toResponse();
	}

	@PutMapping(path = "/{locus}")
	public ResponseEntity<?> putLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel locus
	) {
		StatusResult result = service.saveLocus(taxon, locusId, new Locus(taxon, locus.getId(), locus.getDescription()));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse():
				new PutOutputModel(result.getStatus()).toResponse();
	}

	@DeleteMapping(path = "/{locus}")
	public ResponseEntity<?> deleteLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		return service.deleteLocus(taxon, locus).getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponse() :
				new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


}
