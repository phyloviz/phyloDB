package pt.ist.meic.phylodb.job.model;

public class Job {

	private String name;
	private long delay;
	private long rate;
	private boolean done;
	private boolean cancelled;

	public String getName() {
		return name;
	}
	public long getDelay() {
		return delay;
	}
	public long getRate() {
		return rate;
	}
	public boolean isDone() {
		return done;
	}
	public boolean isCancelled() {
		return cancelled;
	}

	public Job() {
	}

}
