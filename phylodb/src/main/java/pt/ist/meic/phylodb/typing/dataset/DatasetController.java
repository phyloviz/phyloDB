package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/datasets")
	public class DatasetController {

	private DatasetService service;

	public DatasetController(DatasetService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getDatasets() {

		return null;
	}

	@GetMapping(path = "/{dataset}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getDataset(
			@PathVariable("dataset") String dataset
	) {
		return null;
	}

	// profile input model tem que trazer o isolate associado
	@PostMapping(path = "")
	public ResponseEntity postDataset(
			@RequestParam("project") String project
	) {
		return null;
	}

	@PutMapping(path = "/{dataset}")
	public ResponseEntity putDataset(
			@PathVariable("dataset") String datasetId,
			@RequestBody DatasetInputModel dataset
	) {
		return null;
	}

	@DeleteMapping(path = "/{dataset}")
	public ResponseEntity deleteDataset(
			@PathVariable("dataset") String dataset
	) {
		return null;
	}
}
