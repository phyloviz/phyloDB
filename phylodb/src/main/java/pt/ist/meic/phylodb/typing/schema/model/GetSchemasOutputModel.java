package pt.ist.meic.phylodb.typing.schema.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

public class GetSchemasOutputModel implements OutputModel {

	private final List<SchemaOutputModel.Resumed> entities;

	public GetSchemasOutputModel(List<VersionedEntity<Schema.PrimaryKey>> entities) {
		this.entities = entities.stream()
				.map(SchemaOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<SchemaOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
