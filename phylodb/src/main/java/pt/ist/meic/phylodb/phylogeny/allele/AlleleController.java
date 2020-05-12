package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAllelesOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController
@RequestMapping("/taxons/{taxon}/loci/{locus}/alleles")
public class AlleleController extends Controller<Allele> {

	@Value("${application.limits.files.fasta.line}")
	private String lineLength;

	private AlleleService service;

	public AlleleController(AlleleService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, permission = Operation.READ, required = false)
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<?> getAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) UUID project,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type
	) {
		return getAll(type, l -> service.getAlleles(taxonId, locusId, project, page, l),
				GetAllelesOutputModel::new,
				(a) -> new FileOutputModel(new FastaFormatter().format(a, Integer.parseInt(lineLength))));
	}

	@Authorized(role = Role.USER, permission = Operation.READ, required = false)
	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "project", required = false) UUID project,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getAllele(taxonId, locusId, alleleId, project, version), GetAlleleOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@PutMapping(path = "/{allele}")
	public ResponseEntity<?> saveAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "project", required = false) String project,
			@RequestBody AlleleInputModel input
	) {
		return put(() -> input.toDomainEntity(taxonId, locusId, alleleId, project), service::saveAllele);
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> postAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) UUID project,
			@RequestParam("file") MultipartFile file

	) throws IOException {
		return fileStatus(() -> service.saveAllelesOnConflictSkip(taxonId, locusId, project, file));
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@PutMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> putAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) UUID project,
			@RequestParam("file") MultipartFile file

	) throws IOException {
		return fileStatus(() -> service.saveAllelesOnConflictUpdate(taxonId, locusId, project, file));
	}

	@Authorized(role = Role.USER, permission = Operation.WRITE)
	@DeleteMapping(path = "/{allele}")
	public ResponseEntity<?> deleteAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "project", required = false) UUID project
	) {
		return status(() -> service.deleteAllele(taxonId, locusId, alleleId, project));
	}

}
