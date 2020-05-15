package pt.ist.meic.phylodb.job.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetJobsOutputModel implements OutputModel {

	private final List<JobOutputModel> jobs;

	public GetJobsOutputModel(List<Job> jobs) {
		this.jobs = jobs.stream()
				.map(JobOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<JobOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(jobs);
	}
}
