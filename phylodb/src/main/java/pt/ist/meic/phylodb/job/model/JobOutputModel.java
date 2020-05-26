package pt.ist.meic.phylodb.job.model;

public class JobOutputModel {

	private String jobId;
	private boolean completed;
	private boolean cancelled;

	public JobOutputModel() {
	}

	public JobOutputModel(Job status) {
		this.jobId = status.getPrimaryKey().getId();
		this.completed = status.isCompleted();
		this.cancelled = status.isCancelled();
	}

	public String getJobId() {
		return jobId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
