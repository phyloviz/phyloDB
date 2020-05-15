package pt.ist.meic.phylodb.job.model;

import java.util.Objects;
import java.util.UUID;

public class Job {

	private PrimaryKey id;
	private String algorithm;
	private String[] params;
	private boolean completed;
	private boolean cancelled;

	public Job(UUID projectId,UUID jobId, boolean completed, boolean cancelled) {
		this.id = new PrimaryKey(projectId, jobId);
		this.completed = completed;
		this.cancelled = cancelled;
	}

	public Job(UUID projectId,UUID jobId, String algorithm, String[] params) {
		this.id = new PrimaryKey(projectId, jobId);
		this.algorithm = algorithm;
		this.params = params;
	}

	public PrimaryKey getPrimaryKey() {
		return id;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String[] getParams() {
		return params;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isCancelled() {
		return cancelled;
	}


	public static class PrimaryKey {

		private final UUID projectId;
		private final UUID id;

		public PrimaryKey(UUID projectId, UUID id) {
			this.projectId = projectId;
			this.id = id;
		}

		public UUID getProjectId() {
			return projectId;
		}

		public UUID getId() {
			return id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PrimaryKey that = (PrimaryKey) o;
			return Objects.equals(projectId, that.projectId) &&
					Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId, id);
		}

	}

}
