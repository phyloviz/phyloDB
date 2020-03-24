package pt.ist.meic.phylodb.utils.service;

import pt.ist.meic.phylodb.utils.db.Status;

import java.util.UUID;

public class StatusResult {

	private Status status;

	public Status getStatus() {
		return status;
	}

	public StatusResult(Status status) {
		this.status = status;
	}
}
