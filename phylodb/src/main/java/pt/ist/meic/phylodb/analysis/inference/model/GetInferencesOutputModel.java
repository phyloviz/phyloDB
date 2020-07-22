package pt.ist.meic.phylodb.analysis.inference.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetInferencesOutputModel is the output model representation of a set of {@link Inference inferences}
 * <p>
 * A GetInferencesOutputModel is constituted by the {@link #inferences} field that contains the resumed information of each inference.
 * Each resumed information is represented by an {@link InferenceOutputModel.Resumed} object.
 */
public class GetInferencesOutputModel implements OutputModel {

	private final List<InferenceOutputModel.Resumed> inferences;

	public GetInferencesOutputModel(List<Entity<Inference.PrimaryKey>> analyses) {
		this.inferences = analyses.stream()
				.map(InferenceOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<InferenceOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(inferences);
	}

}
