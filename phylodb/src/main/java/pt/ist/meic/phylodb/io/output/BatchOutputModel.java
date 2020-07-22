package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

/**
 * A BatchOutputModel is the output model representation of batch operation response
 * <p>
 * A BatchOutputModel is constituted by the {@link #invalid_lines} and {@link #invalid_entities} fields which contains the number of the invalid lines of the file,
 * and the ids of the invalid entities parsed, respectively.
 */
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BatchOutputModel that = (BatchOutputModel) o;
		return Arrays.equals(invalid_lines, that.invalid_lines) &&
				Arrays.equals(invalid_entities, that.invalid_entities);
	}

	@Override
	public ResponseEntity<?> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

}
