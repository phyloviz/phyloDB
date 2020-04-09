package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileOutputModel implements OutputModel {

	private final String filename;
	private final String data;

	public FileOutputModel(String filename, String data) {
		this.filename = filename;
		this.data = data;
	}

	@Override
	public ResponseEntity<byte[]> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.body(data.getBytes());
	}

}
