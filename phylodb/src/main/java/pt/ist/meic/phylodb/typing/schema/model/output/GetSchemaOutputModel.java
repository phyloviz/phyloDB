package pt.ist.meic.phylodb.typing.schema.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

public class GetSchemaOutputModel implements Json, Output<Json> {

	private DetailedSchemaModel schema;


	public GetSchemaOutputModel(Schema schema) {
		this.schema = new DetailedSchemaModel(schema);
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "version", "deprecated", "type", "description", "loci",})
	private static class DetailedSchemaModel extends OutputModel {

		private String id;
		private String type;
		private String description;
		private Object[] loci;

		public DetailedSchemaModel(Schema schema) {
			super(schema.isDeprecated(), schema.getVersion());
			this.id = schema.getId();
			this.type = schema.getType();
			this.description = schema.getDescription();
			this.loci = schema.getLociIds().toArray();
		}

		public String getId() {
			return id;
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public Object[] getLoci() {
			return loci;
		}

	}

}
