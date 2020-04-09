package pt.ist.meic.phylodb.io.output;

import org.springframework.http.ResponseEntity;

public interface OutputModel {

	ResponseEntity<?> toResponseEntity();

}
