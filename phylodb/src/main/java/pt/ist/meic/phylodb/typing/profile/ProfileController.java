package pt.ist.meic.phylodb.typing.profile;

import javafx.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.output.mediatype.Problem;
import pt.ist.meic.phylodb.output.model.StatusOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.profile.model.input.ProfileInputModel;
import pt.ist.meic.phylodb.typing.profile.model.output.GetProfileOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.output.GetProfilesOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.controller.EntityController;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;
import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@RestController("SequenceTypeController")
@RequestMapping("/datasets/{dataset}/profiles")
public class ProfileController extends EntityController {

	private ProfileService service;

	public ProfileController(ProfileService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity<?> getProfiles(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value="Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type,
			@RequestParam Map<String, String> filters
	) {
		if (page < 0)
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Optional<Pair<Schema, List<Profile>>> optional = service.getProfiles(datasetId, filters, page, Integer.parseInt(jsonLimit));
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				GetProfilesOutputModel.get(type).apply(optional.get().getKey(), optional.get().getValue()).toResponseEntity();
	}

	@GetMapping(path = "/{profile}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProfile(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("profile") String profileId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) int version
	) {
		Optional<Profile> optional = service.getProfile(datasetId, profileId, version);
		return !optional.isPresent() ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new GetProfileOutputModel(optional.get()).toResponseEntity();
	}

	@PutMapping(path = "/{profile}")
	public ResponseEntity<?> putProfile(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("profile") String profileId,
			@RequestBody ProfileInputModel profileInputModel
	) {
		Optional<Profile> optionalProfile = profileInputModel.toDomainEntity(datasetId.toString(), profileId);
		if(!optionalProfile.isPresent())
			return new ErrorOutputModel(Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST).toResponseEntity();
		Status result = service.saveProfile(optionalProfile.get());
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postProfiles(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam("method") String method,
			@RequestParam("taxon") String taxonId,
			@RequestParam("schema") String schemaId,
			@RequestBody MultipartFile file
	) throws FileFormatException {
		Status result = service.saveProfilesOnConflictSkip(datasetId, method, taxonId, schemaId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@PutMapping(path = "")
	public ResponseEntity<?> putProfiles(
			@PathVariable("dataset") UUID datasetId,
			@RequestParam("method") String method,
			@RequestParam("taxon") String taxonId,
			@RequestParam("schema") String schemaId,
			@RequestBody MultipartFile file
	) throws FileFormatException {
		Status result = service.saveProfilesOnConflictUpdate(datasetId, method, taxonId, schemaId, file);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

	@DeleteMapping(path = "/{profile}")
	public ResponseEntity<?> deleteProfile(
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("profile") String profileId
	) {
		Status result = service.deleteProfile(datasetId, profileId);
		return result.equals(UNCHANGED) ?
				new ErrorOutputModel(Problem.UNAUTHORIZED, HttpStatus.UNAUTHORIZED).toResponseEntity() :
				new StatusOutputModel(result).toResponseEntity();
	}

}
