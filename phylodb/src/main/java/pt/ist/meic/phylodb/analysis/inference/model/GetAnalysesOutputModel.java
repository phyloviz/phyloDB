package pt.ist.meic.phylodb.analysis.inference.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetAnalysesOutputModel implements OutputModel {

	private final List<AnalysisOutputModel> analyses;

	public GetAnalysesOutputModel(List<Analysis> analyses) {
		this.analyses = analyses.stream()
				.map(AnalysisOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<AnalysisOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(analyses);
	}

}
