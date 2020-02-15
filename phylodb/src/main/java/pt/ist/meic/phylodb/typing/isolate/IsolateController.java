package pt.ist.meic.phylodb.typing.isolate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.typing.isolate.model.IsolateInputModel;

@RestController
@RequestMapping("/datasets/{dataset}/isolates")
public class IsolateController {

	private IsolateService service;

	public IsolateController(IsolateService service) {
		this.service = service;
	}

	// Filtered by ancillary data
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity getIsolates(
			@PathVariable("dataset") String datasets,
			@RequestParam("page") int page,
			@RequestParam("size") int size
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getIsolate(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolate
	) {
		return null;
	}

	@GetMapping(path = "/{isolate}/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getIsolateProfiles(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolate
	) {
		return null;
	}

	// tem que trazer o isolate e o ST para criar a ligação entre eles
	@PostMapping(path = "")
	public ResponseEntity postIsolate(
			@PathVariable("dataset") String datasets,
			@RequestBody(required = false) IsolateInputModel isolate,
			@RequestParam(value = "file", required = false) MultipartFile file
	) {
		return null;
	}

	@PutMapping(path = "/{isolate}/profiles/{profile}")
	public ResponseEntity relateIsolateToProfile(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolate,
			@PathVariable("profile") String profile
	) {
		return null;
	}

	@PutMapping(path = "/{isolate}")
	public ResponseEntity deleteIsolate(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolateId,
			@RequestBody(required = false) IsolateInputModel isolate
	) {
		return null;
	}

	@DeleteMapping(path = "/{isolate}")
	public ResponseEntity deleteIsolate(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolate
	) {
		return null;
	}

	@DeleteMapping(path = "/{isolate}/profiles/{profile}")
	public ResponseEntity unrelateIsolateToProfile(
			@PathVariable("dataset") String datasets,
			@PathVariable("isolate") String isolate,
			@PathVariable("profile") String profile
	) {
		return null;
	}

}
