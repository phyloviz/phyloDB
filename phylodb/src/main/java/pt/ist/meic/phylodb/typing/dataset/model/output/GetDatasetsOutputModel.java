package pt.ist.meic.phylodb.typing.dataset.model.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetDatasetsOutputModel implements Json, Output<Json> {

	private List<SimpleSchemaModel> datasets;

	public GetDatasetsOutputModel(List<Dataset> datasets) {
		this.datasets = datasets.stream()
				.map(SimpleSchemaModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleSchemaModel> getDatasets() {
		return datasets;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class SimpleSchemaModel {

		private UUID id;

		public SimpleSchemaModel(Dataset dataset) {
			this.id = dataset.getId();
		}

		public UUID getId() {
			return id;
		}

	}

}
