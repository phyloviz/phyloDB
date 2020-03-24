package pt.ist.meic.phylodb.typing.dataset.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

import java.util.UUID;

public class GetDatasetOutputModel implements Json, Output<Json> {

	private DetailedDatasetModel dataset;

	public GetDatasetOutputModel(Dataset dataset) {
		this.dataset = new DetailedDatasetModel(dataset);
	}

	public DetailedDatasetModel getDataset() {
		return dataset;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class DetailedDatasetModel {

		private UUID id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String description;
		private String taxonId;
		private String schemaId;

		public DetailedDatasetModel(Dataset dataset) {
			this.id = dataset.getId();
			this.description = dataset.getDescription();
			this.taxonId = dataset.getTaxonId();
			this.schemaId = dataset.getSchemaId();
		}

		public UUID getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public String getTaxonId() {
			return taxonId;
		}

		public String getSchemaId() {
			return schemaId;
		}

	}

}
