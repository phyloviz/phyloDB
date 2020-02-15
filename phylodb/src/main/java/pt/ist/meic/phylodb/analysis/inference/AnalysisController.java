package pt.ist.meic.phylodb.analysis.inference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/datasets/{dataset}/analyses")
public class AnalysisController {

	private AnalysisService service;

	public AnalysisController(AnalysisService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAnalyses(
			@PathVariable("algorithm") String algorithm,
			@PathVariable("dataset") String dataset
	) {
		return null;
	}

	@GetMapping(path = "/{analysis}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getAnalysis(
			@PathVariable("dataset") String dataset,
			@PathVariable("analysis") String analysis
	) {
		return null;
	}


	@PutMapping(path = "/{analysis}")
	public ResponseEntity putAnalysis(
			@PathVariable("dataset") String dataset,
			@PathVariable("analysis") String analysis,
			@RequestParam("file") MultipartFile file
	) {
		return null;
	}

	@DeleteMapping(path = "/{analysis}")
	public ResponseEntity deleteAnalysis(
			@PathVariable("dataset") String dataset,
			@PathVariable("analysis") String analysis
	) {
		return null;
	}
}
