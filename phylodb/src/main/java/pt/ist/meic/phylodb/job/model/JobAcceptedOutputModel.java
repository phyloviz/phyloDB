package pt.ist.meic.phylodb.job.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

public class JobAcceptedOutputModel implements OutputModel {

	private String job_id;
	private String analysis_id;

	public JobAcceptedOutputModel() {
	}

	public JobAcceptedOutputModel(String job_id, String analysis_id) {
		this.job_id = job_id;
		this.analysis_id = analysis_id;
	}

	public String getJob_id() {
		return job_id;
	}

	public String getAnalysis_id() {
		return analysis_id;
	}

	@Override
	public ResponseEntity<?> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(this);
	}

}
