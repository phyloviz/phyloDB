package pt.ist.meic.phylodb.job.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * A job is used to represent a request to queue an algorithm execution
 * <p>
 * A job is constituted by the {@link #id}, to identify to the job, by the {@link #algorithm} that is the respective algorithm,
 * the {@link #params}, that can be the dataset id in case of being an inference job, or the dataset id and inference id, in case of
 * being a visualization job. It is also composed of {@link #analysisId}, that is the id generated for the result of the job, and the
 * {@link #completed}, and {@link #cancelled} which are the status of the job.
 */
public class Job {

	private final PrimaryKey id;
	private String algorithm;
	private Object[] params;
	private String analysisId;
	private boolean completed;
	private boolean cancelled;

	public Job(String projectId, String jobId, boolean completed, boolean cancelled) {
		this.id = new PrimaryKey(projectId, jobId);
		this.completed = completed;
		this.cancelled = cancelled;
	}

	public Job(String projectId, String jobId, String algorithm, String analysisId, Object[] params) {
		this.id = new PrimaryKey(projectId, jobId);
		this.algorithm = algorithm;
		this.analysisId = analysisId;
		this.params = params;
	}

	public PrimaryKey getPrimaryKey() {
		return id;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public Object[] getParams() {
		return params;
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Job job = (Job) o;
		return Objects.equals(id, job.id) &&
				Objects.equals(algorithm, job.algorithm) &&
				Arrays.equals(params, job.params) &&
				Objects.equals(analysisId, job.analysisId);
	}

	/**
	 * A Job.PrimaryKey is the identification of a job
	 * <p>
	 * A Visualization.PrimaryKey is constituted by the {@link #projectId}, and {@link #id} fields which identify the job.
	 */
	public static class PrimaryKey {

		private final String projectId;
		private final String id;

		public PrimaryKey(String projectId, String id) {
			this.projectId = projectId;
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getId() {
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
