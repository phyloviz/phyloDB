package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SingleOutputModel implements OutputModel {

	private final String id;
	private final int version;
	private final boolean deprecated;

	public SingleOutputModel(String id, int version, boolean deprecated) {
		this.id = id;
		this.version = version;
		this.deprecated = deprecated;
	}

	public String getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public ResponseEntity<SingleOutputModel> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(this);
	}

}
