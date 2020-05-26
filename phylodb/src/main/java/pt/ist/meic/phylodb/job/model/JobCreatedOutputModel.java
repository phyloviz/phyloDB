package pt.ist.meic.phylodb.job.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.UUID;

public class JobCreatedOutputModel implements OutputModel {

	private UUID job_id;
	private UUID analysis_id;

	public JobCreatedOutputModel() {
	}

	public JobCreatedOutputModel(UUID job_id, UUID analysis_id) {
		this.job_id = job_id;
		this.analysis_id = analysis_id;
	}

	public UUID getJob_id() {
		return job_id;
	}

	public UUID getAnalysis_id() {
		return analysis_id;
	}

	@Override
	public ResponseEntity<?> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(this);
	}

}
