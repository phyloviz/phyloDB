package pt.ist.meic.phylodb.analysis.visualization.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetVisualizationsOutputModel is the output model representation of a set of {@link Visualization visualizations}
 * <p>
 * A GetVisualizationsOutputModel is constituted by the {@link #visualizations} field that contains the resumed information of each visualization.
 * Each resumed information is represented by an {@link VisualizationOutputModel.Resumed} object.
 */
public class GetVisualizationsOutputModel implements OutputModel {

	private final List<VisualizationOutputModel.Resumed> visualizations;

	public GetVisualizationsOutputModel(List<Entity<Visualization.PrimaryKey>> entities) {
		this.visualizations = entities.stream()
				.map(VisualizationOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<VisualizationOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(visualizations);
	}

}
