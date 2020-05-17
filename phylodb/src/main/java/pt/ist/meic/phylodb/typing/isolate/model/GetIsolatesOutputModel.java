package pt.ist.meic.phylodb.typing.isolate.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetIsolatesOutputModel implements OutputModel {

	private final List<IsolateOutputModel.Resumed> entities;

	public GetIsolatesOutputModel(List<Isolate> entities) {
		this.entities = entities.stream()
				.map(IsolateOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<IsolateOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
