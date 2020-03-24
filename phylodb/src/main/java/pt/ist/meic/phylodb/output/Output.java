package pt.ist.meic.phylodb.output;

import org.springframework.http.ResponseEntity;

public interface Output<T> {

	ResponseEntity<T> toResponseEntity();
}
