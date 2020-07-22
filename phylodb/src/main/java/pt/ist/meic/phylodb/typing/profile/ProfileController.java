package pt.ist.meic.phylodb.typing.profile;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.io.output.BatchOutputModel;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.profile.model.GetProfileOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.GetProfilesOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.profile.model.ProfileInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION;

/**
 * Class that contains the endpoints to manage profiles
 * <p>
 * The endpoints responsibility is to parse the input, call the respective service, and to format the resulting output.
 */
@RestController("SequenceTypeController")
@RequestMapping("projects/{project}/datasets/{dataset}/profiles")
public class ProfileController extends Controller {

	private ProfileService service;

	public ProfileController(ProfileService service) {
		this.service = service;
	}

	/**
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<GetProfilesOutputModel>} representing the specified profiles page or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllJson(l -> service.getProfilesEntities(projectId, datasetId, page, l), GetProfilesOutputModel::new);
	}

	/**
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param profileId identifier of the {@link Profile profile}
	 * @param version   version of the {@link Profile profile}
	 * @return a {@link ResponseEntity<GetProfileOutputModel>} representing the specified profile or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{profile}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getProfile(projectId, datasetId, profileId, version), GetProfileOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	/**
	 * @param projectId  identifier of the {@link Project project}
	 * @param datasetId  identifier of the {@link Dataset dataset}
	 * @param profileId  identifier of the {@link Profile profile}
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @param input      profile input model
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{profile}")
	public ResponseEntity<?> putProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestBody ProfileInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId, datasetId, profileId), p -> service.saveProfile(p, authorized));
	}

	/**
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param page      number of the page to retrieve
	 * @return a {@link ResponseEntity<FileOutputModel>} representing the specified profiles page in a formatted string or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/files", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<?> getProfilesFile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		return getAllFile(l -> service.getProfiles(projectId, datasetId, page, l), p -> new FileOutputModel(ProfilesFormatter.get(p.getKey().getType().getName()).format(p.getValue(), p.getKey())));
	}

	/**
	 * @param projectId  identifier of the {@link Project project}
	 * @param datasetId  identifier of the {@link Dataset dataset}
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @param file       file with the profiles
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "/files")
	public ResponseEntity<?> postProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		return fileStatus(() -> service.saveProfilesOnConflictSkip(projectId, datasetId, authorized, file));
	}

	/**
	 * @param projectId  identifier of the {@link Project project}
	 * @param datasetId  identifier of the {@link Dataset dataset}
	 * @param authorized boolean which indicates if the alleles used are private or public
	 * @param file       file with the profiles
	 * @return a {@link ResponseEntity<BatchOutputModel>} representing the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 * @throws IOException if there is an error parsing the file
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/files")
	public ResponseEntity<?> putProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		return fileStatus(() -> service.saveProfilesOnConflictUpdate(projectId, datasetId, authorized, file));
	}

	/**
	 * @param projectId identifier of the {@link Project project}
	 * @param datasetId identifier of the {@link Dataset dataset}
	 * @param profileId identifier of the {@link Profile profile}
	 * @return a {@link ResponseEntity<NoContentOutputModel>} representing the status of the result or a {@link ResponseEntity<ErrorOutputModel>} if it couldn't perform the operation
	 */
	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{profile}")
	public ResponseEntity<?> deleteProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId
	) {
		return status(() -> service.deleteProfile(projectId, datasetId, profileId));
	}

}
