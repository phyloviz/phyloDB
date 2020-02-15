package pt.ist.meic.phylodb.typing.dataset;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;

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

	@GetMapping(path = "/{dataset}/schemas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getDatasetSchema(
			@PathVariable("dataset") String dataset
	) {
		return null;
	}

	@PostMapping(path = "")
	public ResponseEntity postDataset(
			@RequestBody DatasetInputModel dataset
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
