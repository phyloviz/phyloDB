package pt.ist.meic.phylodb.typing.dataset.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetDatasetsOutputModel implements OutputModel {

	private final List<DatasetOutputModel> entities;

	public GetDatasetsOutputModel(List<Dataset> entities) {
		this.entities = entities.stream()
				.map(DatasetOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<DatasetOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}



}
