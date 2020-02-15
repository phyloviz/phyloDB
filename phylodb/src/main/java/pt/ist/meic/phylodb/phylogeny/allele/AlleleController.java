package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;

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
			@RequestParam("page") int page,
			@RequestParam("size") int size
	) {
		return null;
	}

	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		return null;
	}

	@PostMapping(path = "")
	public ResponseEntity postAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@RequestBody(required = false) AlleleInputModel allele,
			@RequestParam(value = "file", required = false) MultipartFile file
	) {
		return null;
	}

	@PutMapping(path = "/{allele}")
	public ResponseEntity putAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String alleleId,
			@RequestBody AlleleInputModel allele
	) {
		return null;
	}

	@DeleteMapping(path = "/{allele}")
	public ResponseEntity deleteAllele(
			@PathVariable("taxon") String taxon,
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		return null;
	}

}
