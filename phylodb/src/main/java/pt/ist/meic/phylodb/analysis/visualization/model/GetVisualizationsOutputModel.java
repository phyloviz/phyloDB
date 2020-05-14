package pt.ist.meic.phylodb.analysis.visualization.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetVisualizationsOutputModel implements OutputModel {

	private final List<VisualizationOutputModel> entities;

	public GetVisualizationsOutputModel(List<Visualization> entities) {
		this.entities = entities.stream()
				.map(VisualizationOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<VisualizationOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
