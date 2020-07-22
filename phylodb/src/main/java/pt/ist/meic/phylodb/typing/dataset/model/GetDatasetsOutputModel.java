package pt.ist.meic.phylodb.typing.dataset.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetDatasetsOutputModel is the output model representation of a set of {@link Dataset datasets}
 * <p>
 * A GetDatasetsOutputModel is constituted by the {@link #datasets} field that contains the resumed information of each dataset.
 * Each resumed information is represented by an {@link DatasetOutputModel.Resumed} object.
 */
public class GetDatasetsOutputModel implements OutputModel {

	private final List<DatasetOutputModel.Resumed> datasets;

	public GetDatasetsOutputModel(List<VersionedEntity<Dataset.PrimaryKey>> entities) {
		this.datasets = entities.stream()
				.map(DatasetOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<DatasetOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(datasets);
	}

}
