package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.mediatype.Problem;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.output.GetAlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.output.GetAllelesOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.controller.PutOutputModel;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController
@RequestMapping("/taxons/{taxon}/loci/{locus}/alleles")
public class AlleleController extends EntityController {

	private AlleleService service;

	public AlleleController(AlleleService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity<?> getAlleles(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value="Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type

	) {
		Optional<List<Allele>> optional = service.getAlleles(taxon, locus, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				GetAllelesOutputModel.get(type).apply(optional.get()).toResponse();
	}

	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		Optional<Allele> optional = service.getAllele(taxon, locus, allele);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.NOT_FOUND, HttpStatus.NOT_FOUND).toResponse() :
				new GetAlleleOutputModel(optional.get()).toResponse();
	}

	@PutMapping(path = "/{allele}")
	public ResponseEntity<?> putAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String alleleId,
			@RequestBody AlleleInputModel allele
	) {
		StatusResult result = service.saveAllele(taxon, locus, alleleId, new Allele(taxon, locus, allele.getId(), allele.getSequence()));
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new PutOutputModel(result.getStatus()).toResponse();
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postAlleles(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestBody MultipartFile file

	) throws FileFormatException {
		StatusResult result = service.saveAllelesOnConflictSkip(taxon, locus, file);
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping(path = "")
	public ResponseEntity<?> putAlleles(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestBody MultipartFile file

	) throws FileFormatException {
		StatusResult result = service.saveAllelesOnConflictUpdate(taxon, locus, file);
		return result.getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponse() :
				new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/{allele}")
	public ResponseEntity<?> deleteAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		return service.deleteAllele(taxon, locus, allele).getStatus().equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponse() :
				new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
