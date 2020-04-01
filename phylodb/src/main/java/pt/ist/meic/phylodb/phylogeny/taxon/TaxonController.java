package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.phylogeny.taxon.model.input.TaxonInputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.output.GetTaxonOutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.output.GetTaxonsOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

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
	public ResponseEntity<?> getTaxons(
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return page < 0 ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity() :
				new GetTaxonsOutputModel(service.getTaxons(page, Integer.parseInt(jsonLimit)).get()).toResponseEntity();
	}

	@GetMapping(path = "/{taxon}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestParam(value = "version", defaultValue = "-1") int version
	) {
		Optional<Taxon> optional = service.getTaxon(taxonId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponseEntity() :
				new GetTaxonOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{taxon}")
	public ResponseEntity<?> saveTaxon(
			@PathVariable("taxon") String taxonId,
			@RequestBody TaxonInputModel taxonInputModel
	) {
		Optional<Taxon> taxonOptional = taxonInputModel.toDomainEntity(taxonId);
		return !taxonOptional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity() :
				new StatusOutputModel(service.saveTaxon(taxonOptional.get())).toResponseEntity();
	}

	@DeleteMapping(path = "/{taxon}")
	public ResponseEntity<?> deleteTaxon(
				@PathVariable("taxon") String taxonId
	) {
		Status result = service.deleteTaxon(taxonId);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

}
