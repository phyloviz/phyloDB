package pt.ist.meic.phylodb.typing.schema.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;

public class GetSchemaOutputModel implements Json, Output<Json> {

	private DetailedSchemaModel schema;


	public GetSchemaOutputModel(Schema schema) {
		this.schema = new DetailedSchemaModel(schema);
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class DetailedSchemaModel {

		private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String description;
		private String[] loci;

		public DetailedSchemaModel(Schema schema) {
			this.id = schema.getId();
			this.description = schema.getDescription();
			this.loci = schema.getLociIds();
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public String[] getLoci() {
			return loci;
		}

	}

}
