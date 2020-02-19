package pt.ist.meic.phylodb.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public final ResponseEntity handle(HttpMessageNotReadableException ex) {
		return handle(ex, Problem.BAD_REQUEST, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity handle(Exception ex) {
		return handle(ex, Problem.SERVER, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity handle(Exception ex, String type, HttpStatus status) {
		ex.printStackTrace();
		//LOG.info(ex.getMessage());
		//HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaTypes.APPLICATION_PROBLEM_JSON);
		return new ResponseEntity<>(new ErrorOutputModel(type), status);
	}

}
