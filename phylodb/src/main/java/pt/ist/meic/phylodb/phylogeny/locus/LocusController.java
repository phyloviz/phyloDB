package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.phylogeny.locus.model.LocusInputModel;

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
			@RequestParam("page") int page,
			@RequestParam("size") int size
	) {
		return null;
	}

	@GetMapping(path = "/{locus}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{locus}/schemas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getSchemasByLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		return null;
	}

	@PostMapping(path = "")
	public ResponseEntity postLocus(
			@PathVariable("taxon") String taxon,
			@RequestBody LocusInputModel locus
	) {
		return null;
	}

	@PutMapping(path = "/{locus}")
	public ResponseEntity putLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locusId,
			@RequestBody LocusInputModel locus
	) {
		return null;
	}

	@DeleteMapping(path = "/{locus}")
	public ResponseEntity deleteLocus(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus
	) {
		return null;
	}


}
