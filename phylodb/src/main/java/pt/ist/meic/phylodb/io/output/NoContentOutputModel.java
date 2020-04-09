package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NoContentOutputModel implements OutputModel {

	@Override
	public ResponseEntity<NoContentOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
