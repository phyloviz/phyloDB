package pt.ist.meic.phylodb.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * An ExceptionHandlerAdvice is a ControllerAdvice to perform exception handling
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

	/**
	 * Exceptions that are caught by this ExceptionHandler are handled as an user error
	 *
	 * @param ex HttpMediaTypeNotAcceptableException caught by the handler
	 * @return a {@link ResponseEntity<ErrorOutputModel>} with the {@link HttpStatus#NOT_ACCEPTABLE} status
	 */
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpMediaTypeNotAcceptableException ex) {
		return handle(ex, Problem.NOT_ACCEPTABLE);
	}

	/**
	 * Exceptions that are caught by this ExceptionHandler are handled as an user error
	 *
	 * @param ex BindException caught by the handler
	 * @return a {@link ResponseEntity<ErrorOutputModel>} with the {@link HttpStatus#BAD_REQUEST} status
	 */
	@ExceptionHandler(BindException.class)
	public final ResponseEntity<ErrorOutputModel> handle(BindException ex) {
		return handle(ex, Problem.BODY_TYPE);
	}

	/**
	 * Exceptions that are caught by this ExceptionHandler are handled as an user error
	 *
	 * @param ex HttpRequestMethodNotSupportedException caught by the handler
	 * @return a {@link ResponseEntity<ErrorOutputModel>} with the {@link HttpStatus#METHOD_NOT_ALLOWED} status
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public final ResponseEntity<ErrorOutputModel> handle(HttpRequestMethodNotSupportedException ex) {
		return handle(ex, Problem.NOT_ALLOWED);
	}

	/**
	 * Exceptions that are caught by this ExceptionHandler are handled as an user error
	 *
	 * @param ex exception caught by the handler. This exception can be HttpMessageNotReadableException, MissingServletRequestParameterException, MissingServletRequestPartException,
	 *           MultipartException, MethodArgumentTypeMismatchException, or an HttpMediaTypeNotSupportedException exception
	 * @return a {@link ResponseEntity<ErrorOutputModel>} with the {@link HttpStatus#BAD_REQUEST} status
	 */
	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MissingServletRequestPartException.class,
			MultipartException.class,
			MethodArgumentTypeMismatchException.class,
			HttpMediaTypeNotSupportedException.class
	})
	public final ResponseEntity<ErrorOutputModel> handle400(Exception ex) {
		return handle(ex, Problem.BAD_REQUEST);
	}

	/**
	 * Exceptions that are caught by this ExceptionHandler are handled as a system error
	 *
	 * @param ex any other exception caught by the handler
	 * @return a {@link ResponseEntity<ErrorOutputModel>} with the {@link HttpStatus#INTERNAL_SERVER_ERROR} status
	 */
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorOutputModel> handle500(Exception ex) {
		return handle(ex, Problem.SERVER);
	}

	private ResponseEntity<ErrorOutputModel> handle(Exception ex, Problem problem) {
		ex.printStackTrace();
		LOG.info(ex.getMessage());
		return new ErrorOutputModel(problem).toResponseEntity();
	}

}
