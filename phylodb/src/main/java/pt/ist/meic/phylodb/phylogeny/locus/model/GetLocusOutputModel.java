package pt.ist.meic.phylodb.phylogeny.locus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;

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

	private static class DetailedLocusModel {

		private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String description;

		public DetailedLocusModel(Locus locus) {
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
