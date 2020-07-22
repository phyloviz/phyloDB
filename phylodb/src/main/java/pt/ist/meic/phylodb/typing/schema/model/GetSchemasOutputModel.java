package pt.ist.meic.phylodb.typing.schema.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetSchemasOutputModel is the output model representation of a set of {@link Schema schemas}
 * <p>
 * A GetSchemasOutputModel is constituted by the {@link #schemas} field that contains the resumed information of each schema.
 * Each resumed information is represented by an {@link SchemaOutputModel.Resumed} object.
 */
public class GetSchemasOutputModel implements OutputModel {

	private final List<SchemaOutputModel.Resumed> schemas;

	public GetSchemasOutputModel(List<VersionedEntity<Schema.PrimaryKey>> entities) {
		this.schemas = entities.stream()
				.map(SchemaOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<SchemaOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(schemas);
	}

}
