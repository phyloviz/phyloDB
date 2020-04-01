package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.input.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.output.GetAlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.output.GetAllelesOutputModel;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;
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
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value="Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type

	) {
		if (page < 0)
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Optional<List<Allele>> optional = service.getAlleles(taxonId, locusId, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				GetAllelesOutputModel.get(type).apply(optional.get()).toResponseEntity();
	}

	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		Optional<Allele> optional = service.getAllele(taxonId, locusId, alleleId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new GetAlleleOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{allele}")
	public ResponseEntity<?> putAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestBody AlleleInputModel alleleInputModel
	) {
		Optional<Allele> optionalAllele = alleleInputModel.toDomainEntity(taxonId, locusId, alleleId);
		if (!optionalAllele.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveAllele(optionalAllele.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@DeleteMapping(path = "/{allele}")
	public ResponseEntity<?> deleteAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId
	) {
		Status result = service.deleteAllele(taxonId, locusId, alleleId);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PostMapping(path = "/files")
	public ResponseEntity<?> postAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestBody MultipartFile file

	) throws FileFormatException {
		Status result = service.saveAllelesOnConflictSkip(taxonId, locusId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PutMapping(path = "/files")
	public ResponseEntity<?> putAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestBody MultipartFile file

	) throws FileFormatException {
		Status result = service.saveAllelesOnConflictUpdate(taxonId, locusId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

}
