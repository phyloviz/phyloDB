package pt.ist.meic.phylodb.analysis.inference.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetInferencesOutputModel implements OutputModel {

	private final List<InferenceOutputModel> analyses;

	public GetInferencesOutputModel(List<Inference> analyses) {
		this.analyses = analyses.stream()
				.map(InferenceOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<InferenceOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(analyses);
	}

}
