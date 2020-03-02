package pt.ist.meic.phylodb.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.mediatype.Problem;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

	@ExceptionHandler({HttpMessageNotReadableException.class, FileFormatException.class})
	public final ResponseEntity handle(HttpMessageNotReadableException ex) {
		return handle(ex, Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity handle(Exception ex) {
		return handle(ex, Problem.SERVER, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity handle(Exception ex, String type, HttpStatus status) {
		ex.printStackTrace();
		LOG.info(ex.getMessage());
		//HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaTypes.APPLICATION_PROBLEM_JSON);
		return new ErrorOutputModel(type, status).toResponse();
	}

}
