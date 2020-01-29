package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taxons")
	public class TaxonController {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	// params can include page, size, and key values for ancillary data
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getTaxons() {
		return null;
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getTaxon(
			@PathVariable("taxon") String taxon
	) {
		return null;
	}

	// profile input model tem que trazer o isolate associado
	@PostMapping(path = "")
	public ResponseEntity postTaxon(
			@RequestBody TaxonInputModel taxon
	) {
		return null;
	}

	@PutMapping(path = "/{taxon}")
	public ResponseEntity putTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody DatasetInputModel taxon
	) {
		return null;
	}

	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity deleteTaxon(
			@PathVariable("taxon") String taxon
	) {
		return null;
	}
}
