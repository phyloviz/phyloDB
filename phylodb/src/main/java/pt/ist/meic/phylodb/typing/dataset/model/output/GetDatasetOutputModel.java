package pt.ist.meic.phylodb.typing.dataset.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Reference;

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

	@JsonPropertyOrder({ "id", "version", "deprecated", "description", "taxon_id", "schema_id", "schema_version", "schema_deprecated" })
	private static class DetailedDatasetModel extends OutputModel {

		private UUID id;
		private String description;
		private String taxon_id;
		private String schema_id;
		private int schema_version;
		private boolean schema_deprecated;

		public DetailedDatasetModel(Dataset dataset) {
			super(dataset.isDeprecated(), dataset.getVersion());
			this.id = dataset.getId();
			this.description = dataset.getDescription();
			Reference<Schema.PrimaryKey> schemaReference = dataset.getSchema();
			this.taxon_id = schemaReference.getId().getTaxonId();
			this.schema_id = schemaReference.getId().getId();
			this.schema_version = schemaReference.getVersion();
			this.schema_deprecated = schemaReference.isDeprecated();
		}

		public UUID getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public String getTaxon_id() {
			return taxon_id;
		}

		public String getSchema_id() {
			return schema_id;
		}

		public int getSchema_version() {
			return schema_version;
		}

		public boolean isSchema_deprecated() {
			return schema_deprecated;
		}

	}

}
