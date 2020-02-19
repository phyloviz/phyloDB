package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAllelesOutputModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taxons/{taxon}/loci/{locus}/alleles")
public class AlleleController {

	private AlleleService service;

	public AlleleController(AlleleService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity getAlleles(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		Optional<List<Allele>> optional = service.getAlleles(taxon, locus, page);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetAllelesOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		Optional<Allele> optional = service.getAllele(taxon, locus, allele);
		return optional.isPresent() ?
				new ResponseEntity<>(new GetAlleleOutputModel(optional.get()), HttpStatus.OK) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.NOT_FOUND), HttpStatus.NOT_FOUND);
	}

	@PostMapping(path = "")
	public ResponseEntity postAlleles(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestBody MultipartFile file

	) throws IOException {
		return service.saveAlleles(taxon, locus, file) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@PutMapping(path = "/{allele}")
	public ResponseEntity putAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String alleleId,
			@RequestBody AlleleInputModel allele
	) {
		return service.saveAllele(taxon, locus, alleleId, new Allele(taxon, locus, allele.getId(), allele.getSequence())) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping(path = "/{allele}")
	public ResponseEntity deleteAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		return service.deleteAllele(taxon, locus, allele) ?
				new ResponseEntity<>(HttpStatus.NO_CONTENT) :
				new ResponseEntity<>(new ErrorOutputModel(Problem.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
	}

}
