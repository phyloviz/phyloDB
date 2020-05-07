package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BatchOutputModel implements OutputModel {

	private Integer[] invalid_lines;
	private String[] invalid_entities;

	public BatchOutputModel() {
	}

	public BatchOutputModel(Integer[] invalid_lines, String[] invalid_entities) {
		this.invalid_lines = invalid_lines;
		this.invalid_entities = invalid_entities;
	}

	public Integer[] getInvalid_lines() {
		return invalid_lines;
	}

	public String[] getInvalid_entities() {
		return invalid_entities;
	}

	@Override
	public ResponseEntity<?> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

}
