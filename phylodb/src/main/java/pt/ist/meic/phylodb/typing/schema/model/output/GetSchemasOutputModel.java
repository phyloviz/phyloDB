package pt.ist.meic.phylodb.typing.schema.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetSchemasOutputModel implements Json, Output<Json> {

	private List<SimpleSchemaModel> schemas;

	public GetSchemasOutputModel(List<Schema> schemas) {
		this.schemas = schemas.stream()
				.map(SimpleSchemaModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleSchemaModel> getSchemas() {
		return schemas;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "version", "deprecated"})
	private static class SimpleSchemaModel extends OutputModel {

		private String id;

		public SimpleSchemaModel(Schema schema) {
			super(schema.isDeprecated(), schema.getVersion());
			this.id = schema.getId();
		}

		public String getId() {
			return id;
		}

	}

}
