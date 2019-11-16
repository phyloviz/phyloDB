package pt.ist.meic.phylodb.allele;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/loci/{locus}/alleles")
public class AlleleController {

	private AlleleService service;

	public AlleleController(AlleleService service) {
		this.service = service;
	}

	@GetMapping(path = "/")
	public ResponseEntity getAlleles(
			@PathVariable("locus") String locus
	) {
		return null;
	}

	@GetMapping(path = "/{allele}")
	public ResponseEntity getAllele(
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele
	) {
		return null;
	}

	@GetMapping(path = "/{allele}/taxon/{taxon}/schemas/{schema}/profiles")
	public ResponseEntity getProfilesByAllele(
			@PathVariable("locus") String locus,
			@PathVariable("allele") String allele,
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@PostMapping(path = "/")
	public ResponseEntity postAlleles(
			@PathVariable("locus") String locus,
			@RequestParam("file") MultipartFile file
	) {
		return null;
	}


}
