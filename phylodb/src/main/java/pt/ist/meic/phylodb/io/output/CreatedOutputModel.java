package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

public class CreatedOutputModel implements OutputModel {

	private UUID id;

	public CreatedOutputModel() {
	}

	public CreatedOutputModel(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public ResponseEntity<CreatedOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.CREATED).body(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CreatedOutputModel that = (CreatedOutputModel) o;
		return Objects.equals(id, that.id);
	}

}
