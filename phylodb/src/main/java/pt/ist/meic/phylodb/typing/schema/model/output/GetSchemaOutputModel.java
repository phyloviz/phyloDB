package pt.ist.meic.phylodb.typing.schema.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
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

	private static class DetailedSchemaModel {

		private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String type;
		private String description;
		private String[] loci;

		public DetailedSchemaModel(Schema schema) {
			this.id = schema.getId();
			this.type = schema.getType();
			this.description = schema.getDescription();
			this.loci = schema.getLociIds();
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

		public String[] getLoci() {
			return loci;
		}

	}

}
