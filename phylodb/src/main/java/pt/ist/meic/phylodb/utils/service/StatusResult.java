package pt.ist.meic.phylodb.utils.service;

import pt.ist.meic.phylodb.utils.db.Status;

public class StatusResult {

	private Status status;
	private String createdId;

	public Status getStatus() {
		return status;
	}
	public String getCreatedId() {
		return createdId;
	}

	public StatusResult(Status status) {
		this.status = status;
	}
	public StatusResult(Status result, String createdId) {
		this.status = result;
		this.createdId = createdId;
	}
}
