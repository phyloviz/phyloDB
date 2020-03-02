package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.mediatype.Problem;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.GetTaxonsOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.TaxonInputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.controller.PutOutputModel;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/taxons")
public class TaxonController extends EntityController {

	private TaxonService service;

	public TaxonController(TaxonService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxons(@RequestParam(value = "page", defaultValue = "0") Integer page) {
		Optional<List<Taxon>> optional = service.getTaxons(page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new GetTaxonsOutputModel(optional.get()).toResponse();
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxon(@PathVariable("taxon") String taxon) {
		Optional<Taxon> optional = service.getTaxon(taxon);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponse() :
				new GetTaxonOutputModel(optional.get()).toResponse();
	}

	@PutMapping(path = "/{taxon}")
	public ResponseEntity<?> putTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel taxon
	) {
		StatusResult result = service.saveTaxon(taxonId, new Taxon(taxon.getId(), taxon.getDescription()));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new PutOutputModel(result.getStatus()).toResponse();
	}

	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity<?> deleteTaxon(
			@PathVariable("taxon") String taxon
	) {
		return service.deleteTaxon(taxon).getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponse() :
				new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
