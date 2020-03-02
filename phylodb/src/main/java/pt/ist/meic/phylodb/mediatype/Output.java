package pt.ist.meic.phylodb.mediatype;

import org.springframework.http.ResponseEntity;

public interface Output<T> {

	ResponseEntity<T> toResponse();
}
