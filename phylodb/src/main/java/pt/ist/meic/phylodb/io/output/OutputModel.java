package pt.ist.meic.phylodb.io.output;

import org.springframework.http.ResponseEntity;

/**
 * The OutputModel interface specifies that each output model must implement a method to transform itself in a {@link ResponseEntity}
 */
public interface OutputModel {

	/**
	 * Creates an response entity of an output model with a body and a status code
	 *
	 * @return a {@link ResponseEntity} with an {@link org.springframework.http.HttpStatus status} and a body embedded
	 */
	ResponseEntity<?> toResponseEntity();

}
