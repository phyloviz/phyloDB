package pt.ist.meic.phylodb.output.mediatype;

public class Problem implements MediaType {

	public static final String NOT_FOUND = "not-found";
	public static final String NOT_ACCEPTABLE = "not-acceptable";
	public static final String NOT_ALLOWED = "not-allowed";
	public static final String CONTENT_TYPE = "content-type";
	public static final String PARAMETER_TYPE = "parameter-type";
	public static final String BODY_TYPE = "body-type";
	public static final String SERVER = "server";
	public static final String UNAUTHORIZED = "unauthorized";
	public static final String FORBIDDEN = "forbidden";
	public static final String BAD_REQUEST = "bad-request";

	private String message;

	public Problem() {
	}

	public Problem(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
