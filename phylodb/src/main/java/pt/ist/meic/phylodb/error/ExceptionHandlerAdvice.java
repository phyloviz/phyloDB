package pt.ist.meic.phylodb.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpMediaTypeNotSupportedException ex) {
		return handle(ex, Problem.CONTENT_TYPE);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpMediaTypeNotAcceptableException ex) {
		return handle(ex, Problem.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public final ResponseEntity<ErrorOutputModel> handle(MethodArgumentTypeMismatchException ex) {
		return handle(ex, Problem.PARAMETER_TYPE);
	}

	@ExceptionHandler(BindException.class)
	public final ResponseEntity<ErrorOutputModel> handle(BindException ex) {
		return handle(ex, Problem.BODY_TYPE);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpRequestMethodNotSupportedException ex) {
		return handle(ex, Problem.NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpMessageNotReadableException ex) {
		return handle(ex, Problem.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorOutputModel> handle(Exception ex) {
		return handle(ex, Problem.SERVER);
	}

	private ResponseEntity<ErrorOutputModel> handle(Exception ex, Problem problem) {
		ex.printStackTrace();
		LOG.info(ex.getMessage());
		return new ErrorOutputModel(problem).toResponseEntity();
	}

}
