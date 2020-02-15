package pt.ist.meic.phylodb.error;

public class ErrorOutputModel {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ErrorOutputModel() {
	}

	public ErrorOutputModel(String message) {
		this.message = message;
	}


}
