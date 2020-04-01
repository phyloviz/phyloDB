package pt.ist.meic.phylodb.phylogeny.locus.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

public class GetLocusOutputModel implements Json, Output<Json> {

	private DetailedLocusModel locus;

	public GetLocusOutputModel() {
	}

	public GetLocusOutputModel(Locus locus) {
		this.locus = new DetailedLocusModel(locus);
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "description", "version", "deprecated"})
	private static class DetailedLocusModel extends OutputModel {

		private String id;
		private String description;

		public DetailedLocusModel(Locus locus) {
			super(locus.isDeprecated(), locus.getVersion());
			this.id = locus.getId();
			this.description = locus.getDescription();
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

	}
}
