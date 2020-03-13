package pt.ist.meic.phylodb.utils.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.UUID;

public class StatusOutputModel implements Json, Output<Json> {

	@JsonIgnore
	private Status status;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private UUID id;

	public StatusOutputModel(Status status) {
		this.status = status;
	}

	public StatusOutputModel(Status status, UUID id) {
		this.status = status;
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return status.equals(Status.CREATED) ?
				ResponseEntity.status(HttpStatus.CREATED).body(this) :
				ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
