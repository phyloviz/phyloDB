package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleInputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAlleleOutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.GetAllelesOutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage alleles
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController
@RequestMapping("/taxa/{taxon}/loci/{locus}/alleles")
public class AlleleController extends Controller {

	@Value("${application.limits.files.fasta.line}")
	private String lineLength;

	private AlleleService service;

	public AlleleController(AlleleService service) {
		this.service = service;
	}

	/**
	 * Endpoint to retrieve the specified page of {@link Allele allele}.
	 * <p>
	 * Returns the page with resumed information of each allele. It requires the user to
	 * be authenticated, and if a project id is passed, to have access to the project.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param page    number of the page to retrieve
	 * @return a {@link ResponseEntity<GetAllelesOutputModel>} representing the specified alleles page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ, required = false)
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getAllelesEntities(taxonId, locusId, project, page, l), GetAllelesOutputModel::new);
	}

	/**
	 * Endpoint to retrieve the specified {@link Allele allele}.
	 * <p>
	 * Returns all information of the specified allele. It requires the user to
	 * be authenticated, and if a project id is passed, to have access to the project.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param locusId  identifier of the {@link Locus locus}
	 * @param alleleId identifier of the {@link Allele allele}
	 * @param project  identifier of the {@link Project project} (optional)
	 * @param version  version of the {@link Allele allele}
	 * @return a {@link ResponseEntity<GetAlleleOutputModel>} representing the specified allele or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ, required = false)
	@GetMapping(path = "/{allele}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getAllele(taxonId, locusId, alleleId, project, version), GetAlleleOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * Endpoint to store the given {@link Allele allele}.
	 * <p>
	 * Saves an allele by parsing the input model. It requires the user to be an admin,
	 * or if a project id is passed, to have access to the project.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param locusId  identifier of the {@link Locus locus}
	 * @param alleleId identifier of the {@link Allele allele}
	 * @param project  identifier of the {@link Project project} (optional)
	 * @param input    allele input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
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

	/**
	 * Endpoint to retrieve the specified page of {@link Allele allele} in a fasta formatted string.
	 * <p>
	 * Returns the page in a fasta formatted string. It requires the user to
	 * be authenticated, and if a project id is passed, to have access to the project.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param page    number of the page to retrieve
	 * @return a {@link ResponseEntity<FileOutputModel>} representing the specified alleles page in a formatted string or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ, required = false)
	@GetMapping(path = "/files", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> getAllelesFile(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllFile(l -> service.getAlleles(taxonId, locusId, project, page, l), (a) -> new FileOutputModel(new FastaFormatter().format(a, Integer.parseInt(lineLength))));
	}

	/**
	 * Endpoint to create several {@link Allele alleles}.
	 * <p>
	 * Create the alleles represented in the file if they don't exist. It requires the user to be an admin
	 * or if a project id is passed, to have access to the project.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param file    file with the alleles in the fasta format
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> postAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam("file") MultipartFile file

	) throws IOException {
		return fileStatus(() -> service.saveAllelesOnConflictSkip(taxonId, locusId, project, file));
	}

	/**
	 * Endpoint to save several {@link Allele alleles}.
	 * <p>
	 * Saves the alleles represented in the file. It requires the user to be an admin
	 * or if a project id is passed, to have access to the project.
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param file    file with the alleles in the fasta format
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> putAlleles(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam("file") MultipartFile file

	) throws IOException {
		return fileStatus(() -> service.saveAllelesOnConflictUpdate(taxonId, locusId, project, file));
	}

	/**
	 * Endpoint to deprecate the specified {@link Allele allele}.
	 * <p>
	 * Removes the specified allele. It requires the user to be an admin
	 * or if a project id is passed, to have access to the project.
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param locusId  identifier of the {@link Locus locus}
	 * @param alleleId identifier of the {@link Allele allele}
	 * @param project  identifier of the {@link Project project} (optional)
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{allele}")
	public ResponseEntity<?> deleteAllele(
			@PathVariable("taxon") String taxonId,
			@PathVariable("locus") String locusId,
			@PathVariable("allele") String alleleId,
			@RequestParam(value = "project", required = false) String project
	) {
		return status(() -> service.deleteAllele(taxonId, locusId, alleleId, project));
	}

}
