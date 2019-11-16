package pt.ist.meic.phylodb.profile;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("SequenceTypeController")
@RequestMapping("taxon/{taxon}/schemas/{schema}/profiles")
public class ProfileController {

	private ProfileService service;

	public ProfileController(ProfileService service) {
		this.service = service;
	}

	// Filtered by ancillary data
	@GetMapping(path = "/")
	public ResponseEntity getProfiles(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema
	) {
		return null;
	}

	@GetMapping(path = "/{profile}")
	public ResponseEntity getProfile(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@PathVariable("profile") String profile
	) {
		return null;
	}

	@PostMapping(path = "/")
	public ResponseEntity postProfiles(
			@PathVariable("taxon") String taxon,
			@PathVariable("schema") String schema,
			@RequestParam("file") MultipartFile file
	) {
		return null;
	}
}
