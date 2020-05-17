package pt.ist.meic.phylodb.analysis.visualization.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetVisualizationsOutputModel implements OutputModel {

	private final List<VisualizationOutputModel.Resumed> visualizations;

	public GetVisualizationsOutputModel(List<Visualization> entities) {
		this.visualizations = entities.stream()
				.map(VisualizationOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<VisualizationOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(visualizations);
	}

}
