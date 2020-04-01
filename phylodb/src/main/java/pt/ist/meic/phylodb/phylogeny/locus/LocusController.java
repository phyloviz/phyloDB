package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.input.LocusInputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.output.GetLociOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.output.GetLocusOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;
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
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return page < 0 ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity() :
				new GetLociOutputModel(service.getLoci(taxonId, page, Integer.parseInt(jsonLimit)).get()).toResponseEntity();
	}

	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		Optional<Locus> optional = service.getLocus(taxonId, locusId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponseEntity():
				new GetLocusOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{locus}")
	public ResponseEntity<?> putLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel locusInputModel
	) {
		Optional<Locus> locusOptional = locusInputModel.toDomainEntity(taxonId, locusId);
		if (!locusOptional.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveLocus(locusOptional.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity():
				new StatusOutputModel(result).toResponseEntity();
	}

	@DeleteMapping(path = "/{locus}")
	public ResponseEntity<?> deleteLocus(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId
	) {
		Status result = service.deleteLocus(taxonId, locusId);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}


}
