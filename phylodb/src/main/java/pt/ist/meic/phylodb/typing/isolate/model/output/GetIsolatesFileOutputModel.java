package pt.ist.meic.phylodb.typing.isolate.model.output;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.formatters.dataset.isolate.IsolatesFormatter;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;

import java.util.List;

public class GetIsolatesFileOutputModel implements GetIsolatesOutputModel<byte[]> {

	private List<Isolate> isolates;

	public GetIsolatesFileOutputModel(List<Isolate> isolates) {
		this.isolates = isolates;
	}

	@Override
	public ResponseEntity<byte[]> toResponseEntity() {
		String formatted = new IsolatesFormatter().format(new FileDataset<>(isolates));
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"isolates.txt\"")
				.body(formatted.getBytes());
	}

}
