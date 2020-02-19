package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLociOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.GetLocusOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taxons/{taxon}/loci")
public class LocusController {

	private LocusService service;

	public LocusController(LocusService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getLoci(
			@PathVariable("taxon") String taxon,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		Optional<List<Locus>> optional = service.getLoci(taxon, page);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetLociOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		Optional<Locus> optional = service.getLocus(taxon, locus);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetLocusOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

	@PutMapping(path = "/{locus}")
	public ResponseEntity putLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel locus
	) {
		return service.saveLocus(taxon, locusId, new Locus(taxon, locus.getId(), locus.getDescription())) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping(path = "/{locus}")
	public ResponseEntity deleteLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		return service.deleteLocus(taxon, locus) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
	}


}
