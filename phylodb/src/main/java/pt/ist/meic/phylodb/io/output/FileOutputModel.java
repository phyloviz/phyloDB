package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * A FileOutputModel is the output model representation of file response
 * <p>
 * A FileOutputModel is constituted by the {@link #data} field which contains the file data formatted in a String.
 */
public class FileOutputModel implements OutputModel {

	private String data;

	public FileOutputModel() {
	}

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
