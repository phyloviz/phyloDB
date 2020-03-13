package pt.ist.meic.phylodb.utils.service;

import pt.ist.meic.phylodb.utils.db.Status;

import java.util.UUID;

public class StatusResult {

	private Status status;
	private UUID generatedId;

	public Status getStatus() {
		return status;
	}
	public UUID getGeneratedId() {
		return generatedId;
	}

	public StatusResult(Status status) {
		this.status = status;
	}
	public StatusResult(Status result, UUID createdId) {
		this.status = result;
		this.generatedId = createdId;
	}
}
