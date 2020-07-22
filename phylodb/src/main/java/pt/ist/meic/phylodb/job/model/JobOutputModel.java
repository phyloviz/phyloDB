package pt.ist.meic.phylodb.job.model;

/**
 * A JobOutputModel is an output model for a job
 * <p>
 * A JobOutputModel contains the {@link #id} field which identify the job, and the {@link #completed}, and {@link #cancelled}
 * fields which are the status of the job.
 */
public class JobOutputModel {

	private String id;
	private boolean completed;
	private boolean cancelled;

	public JobOutputModel() {
	}

	public JobOutputModel(Job status) {
		this.id = status.getPrimaryKey().getId();
		this.completed = status.isCompleted();
		this.cancelled = status.isCancelled();
	}

	public String getId() {
		return id;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
