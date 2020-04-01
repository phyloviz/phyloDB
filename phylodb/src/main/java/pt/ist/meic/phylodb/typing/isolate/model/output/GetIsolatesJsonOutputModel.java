package pt.ist.meic.phylodb.typing.isolate.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.List;
import java.util.stream.Collectors;

public class GetIsolatesJsonOutputModel implements Json, GetIsolatesOutputModel<Json> {

	private List<SimpleIsolateModel> isolates;

	public GetIsolatesJsonOutputModel(List<Isolate> isolates) {
		this.isolates = isolates.stream()
				.map(SimpleIsolateModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleIsolateModel> getIsolates() {
		return isolates;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({ "id", "version", "deprecated" })
	private static class SimpleIsolateModel extends OutputModel {

		private String id;

		public SimpleIsolateModel(Isolate isolate) {
			super(isolate.isDeprecated(), isolate.getVersion());
			this.id = isolate.getId();
		}

		public String getId() {
			return id;
		}

	}

}
