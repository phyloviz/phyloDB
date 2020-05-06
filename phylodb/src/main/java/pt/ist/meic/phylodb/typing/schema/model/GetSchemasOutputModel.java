package pt.ist.meic.phylodb.typing.schema.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetSchemasOutputModel implements OutputModel {

	private final List<SchemaOutputModel> entities;

	public GetSchemasOutputModel(List<Schema> entities) {
		this.entities = entities.stream()
				.map(SchemaOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<SchemaOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
