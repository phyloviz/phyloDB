package pt.ist.meic.phylodb.analysis.inference;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.analysis.inference.model.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.GetAnalysesOutputModel;
import pt.ist.meic.phylodb.analysis.inference.model.GetAnalysisOutputModel;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.analysis.TreeFormatter;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("projects/{project}/datasets/{dataset}/analyses")
public class AnalysisController extends Controller<Analysis> {

	private AnalysisService service;

	public AnalysisController(AnalysisService service) {
		this.service = service;
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAnalyses(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page
	) {
		String type = MediaType.APPLICATION_JSON_VALUE;
		return getAll(type, l -> service.getAnalyses(projectId, datasetId, page, l), GetAnalysesOutputModel::new, null);
	}

	@GetMapping(path = "/{analysis}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> getAnalysis(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("analysis") UUID analysisId,
			@RequestParam(value = "format", defaultValue = TreeFormatter.NEWICK) String format
	) {
		if(!format.equals(TreeFormatter.NEWICK) && !format.equals(TreeFormatter.NEXUS))
			return new ErrorOutputModel(Problem.BAD_REQUEST).toResponseEntity();
		return get(() -> service.getAnalysis(projectId, datasetId, analysisId),
				a -> new GetAnalysisOutputModel(a, format),
				() -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@PostMapping(path = "")
	public ResponseEntity<?> postAnalysis(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("algorithm") String algorithm,
			@PathVariable("format") String format,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		Optional<UUID> optional = service.saveAnalysis(projectId, datasetId, algorithm, format, file);
		return  optional.isPresent() ?
				new CreatedOutputModel(optional.get()).toResponseEntity() :
				new ErrorOutputModel(Problem.UNAUTHORIZED).toResponseEntity();
	}

	@DeleteMapping(path = "/{analysis}")
	public ResponseEntity<?> deleteAnalysis(
			@PathVariable("project") UUID projectId,
			@PathVariable("dataset") UUID datasetId,
			@PathVariable("analysis") UUID analysisId
	) {
		return status(() -> service.deleteAnalysis(projectId, datasetId, analysisId));
	}

}
