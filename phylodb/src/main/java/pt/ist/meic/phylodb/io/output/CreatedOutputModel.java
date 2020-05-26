package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class CreatedOutputModel implements OutputModel {

	private String id;

	public CreatedOutputModel() {
	}

	public CreatedOutputModel(String id) {
		this.id = id;
	}

	public String getId() {
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
