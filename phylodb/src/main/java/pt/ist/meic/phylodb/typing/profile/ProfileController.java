package pt.ist.meic.phylodb.typing.profile;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController("SequenceTypeController")
@RequestMapping("/datasets/{dataset}/profiles")
	public class ProfileController {

	private ProfileService service;

	public ProfileController(ProfileService service) {
		this.service = service;
	}

	// params can include page, size, and key values for ancillary data
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity getProfiles(
			@PathVariable("dataset") String dataset,
			@RequestParam Map<String,String> params
	) {
		return null;
	}

	@GetMapping(path = "/{profile}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getProfile(
			@PathVariable("dataset") String dataset,
			@PathVariable("profile") String profile
	) {
		return null;
	}

	@GetMapping(path = "/{profile}/isolates", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getProfileIsolates(
			@PathVariable("dataset") String dataset,
			@PathVariable("profile") String profile
	) {
		return null;
	}

	// profile input model tem que trazer o isolate associado
	@PostMapping(path = "")
	public ResponseEntity postProfile(
			@PathVariable("dataset") String dataset,
			@RequestBody(required = false) ProfileInputModel profile,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "file", required = false) MultipartFile file
	) {
		return null;
	}

	@PutMapping(path = "/{profile}")
	public ResponseEntity putProfile(
			@PathVariable("dataset") String dataset,
			@PathVariable("profile") String profileId,
			@RequestBody(required = false) ProfileInputModel profile
	) {
		return null;
	}

	@DeleteMapping(path = "/{profile}")
	public ResponseEntity deleteProfile(
			@PathVariable("dataset") String dataset,
			@PathVariable("profile") String profile
	) {
		return null;
	}
}
