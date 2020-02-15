package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonsOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taxons")
	public class TaxonController {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getTaxons(@RequestParam(value = "page", defaultValue = "0") Integer page ) {
		Optional<List<Taxon>> optional = service.getTaxons(page);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetTaxonsOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getTaxon(@PathVariable("taxon") String taxon) {
		Optional<Taxon> optional = service.getTaxon(taxon);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetTaxonOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

	@PutMapping(path = "/{taxon}")
	public ResponseEntity putTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel taxon
	) {
		return service.saveTaxon(taxonId, new Taxon(taxon.getId(), taxon.getDescription())) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity deleteTaxon(
			@PathVariable("taxon") String taxon
	) {
		return service.deleteTaxon(taxon) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
	}
}
