package pt.ist.meic.phylodb.job.model;

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
