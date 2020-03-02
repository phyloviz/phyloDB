package pt.ist.meic.phylodb.phylogeny.locus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;

public class GetLocusOutputModel implements Json, Output<Json> {

	private String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	public GetLocusOutputModel() {
	}

	public GetLocusOutputModel(Locus locus) {
		this.id = locus.getId();
		this.description = locus.getDescription();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}
}
