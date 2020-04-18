package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileOutputModel implements OutputModel {

	private final String data;

	public FileOutputModel(String data) {
		this.data = data;
	}

	@Override
	public ResponseEntity<String> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body(data);
	}

}
