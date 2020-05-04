package pt.ist.meic.phylodb.error;

import org.springframework.http.HttpStatus;

public enum Problem {

	SERVER(HttpStatus.INTERNAL_SERVER_ERROR),
	NOT_FOUND(HttpStatus.NOT_FOUND),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
	INVALID_REQUEST(HttpStatus.UNAUTHORIZED),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
	BAD_REQUEST(HttpStatus.BAD_REQUEST),
	CONTENT_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	BODY_TYPE(HttpStatus.BAD_REQUEST),
	NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),
	NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED);

	private final HttpStatus status;

	Problem(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return name().toLowerCase();
	}

	public HttpStatus getStatus() {
		return status;
	}
}
