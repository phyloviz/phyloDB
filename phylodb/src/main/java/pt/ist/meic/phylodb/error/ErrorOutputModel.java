package pt.ist.meic.phylodb.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

public class ErrorOutputModel implements OutputModel {

	private static final String URI = "/problems/%s";

	private final String message;
	private final HttpStatus status;

	public ErrorOutputModel(Problem problem) {
		this.message = String.format(URI, problem.getMessage());
		this.status = problem.getStatus();
	}

	public String getMessage() {
		return message;
	}

	@Override
	public ResponseEntity<ErrorOutputModel> toResponseEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
		return ResponseEntity.status(status).headers(headers).body(this);
	}

}
