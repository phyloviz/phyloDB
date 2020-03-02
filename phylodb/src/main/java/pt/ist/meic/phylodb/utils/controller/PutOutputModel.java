package pt.ist.meic.phylodb.utils.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;
import pt.ist.meic.phylodb.utils.db.Status;

public class PutOutputModel implements Json, Output<Json> {

	@JsonIgnore
	private Status status;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String id;

	public PutOutputModel(Status status) {
		this.status = status;
	}

	public PutOutputModel(Status status, String id) {
		this.status = status;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return status.equals(Status.CREATED) ?
				ResponseEntity.status(HttpStatus.CREATED).body(this) :
				ResponseEntity.status(HttpStatus.NO_CONTENT).body(this);
	}

}
