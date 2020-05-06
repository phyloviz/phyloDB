package pt.ist.meic.phylodb.typing.isolate.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetIsolatesOutputModel implements OutputModel {

	private final List<IsolateOutputModel> entities;

	public GetIsolatesOutputModel(List<Isolate> entities) {
		this.entities = entities.stream()
				.map(IsolateOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<IsolateOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
