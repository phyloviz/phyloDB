package pt.ist.meic.phylodb.typing.ancillary;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.typing.ancillary.model.AncillaryInputModel;

@RestController
@RequestMapping("/datasets/{dataset}/isolates/{isolate}/ancillary}")
public class AncillaryController {

	private AncillaryService service;

	public AncillaryController(AncillaryService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAncillaries(
			@PathVariable("dataset") String dataset,
			@PathVariable("isolate") String isolate
	) {
		return null;
	}

	@GetMapping(path = "/{ancillary}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAncillary(
			@PathVariable("dataset") String dataset,
			@PathVariable("isolate") String isolate,
			@PathVariable("ancillary") String ancillary
	) {
		return null;
	}

	@PostMapping(path = "")
	public ResponseEntity postAncillary(
			@PathVariable("dataset") String dataset,
			@PathVariable("isolate") String isolate,
			@RequestBody AncillaryInputModel ancillary
	) {
		return null;
	}

	@PutMapping(path = "/{ancillary}")
	public ResponseEntity putAncillary(
			@PathVariable("dataset") String dataset,
			@PathVariable("isolate") String isolate,
			@RequestBody AncillaryInputModel ancillary
	) {
		return null;
	}

	@DeleteMapping(path = "/{ancillary}")
	public ResponseEntity deleteAncillary(
			@PathVariable("dataset") String dataset,
			@PathVariable("isolate") String isolate,
			@PathVariable("ancillary") String ancillary
	) {
		return null;
	}

}
