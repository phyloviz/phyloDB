package pt.ist.meic.phylodb.job.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetJobsOutputModel is the output model representation of a set of {@link Job jobs}
 * <p>
 * A GetJobsOutputModel is constituted by the {@link #jobs} field that contains the information of each job.
 * Each information is represented by an {@link JobOutputModel} object.
 */
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
