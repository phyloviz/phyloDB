package pt.ist.meic.phylodb.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.MediaType;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Problem;

public class ErrorOutputModel implements Output {

	private String message;
	private HttpStatus status;

	public ErrorOutputModel() {
	}

	public ErrorOutputModel(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public ResponseEntity<MediaType> toResponseEntity() {
		return ResponseEntity.status(status)
				.body(new Problem(message));
	}

}
