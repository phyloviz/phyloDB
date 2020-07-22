package pt.ist.meic.phylodb.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.Objects;

/**
 * A ErrorOutputModel is the output model representation of an error
 * <p>
 * A ErrorOutputModel is constituted by the {@link #message}, and {@link #status} fields to identify the error.
 */
public class ErrorOutputModel implements OutputModel {

	private static final String URI = "/problems/%s";

	private String message;
	private HttpStatus status;

	public ErrorOutputModel() {
	}

	public ErrorOutputModel(String message) {
		this.message = String.format(URI, message);
	}

	public ErrorOutputModel(Problem problem) {
		this.message = String.format(URI, problem.getMessage());
		this.status = problem.getStatus();
	}

	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ErrorOutputModel that = (ErrorOutputModel) o;
		return Objects.equals(message, that.message) &&
				Objects.equals(status, that.status);
	}

	/**
	 * Creates an response entity of this output model
	 *
	 * @return this ErrorOutputModel object embedded in a {@link ResponseEntity} with the {@link #status status}
	 */
	@Override
	public ResponseEntity<ErrorOutputModel> toResponseEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
		return ResponseEntity.status(status).headers(headers).body(this);
	}

}
