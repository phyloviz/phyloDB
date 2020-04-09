package pt.ist.meic.phylodb.security.authentication;

public abstract class TokenInfo {

	protected boolean active;
	protected String client_id;
	protected String username;
	protected String scope;
	protected String sub;
	protected String aud;
	protected String iss;
	protected String iat;
	protected String exp;

	public TokenInfo() {
	}

	public boolean isActive() {
		return active;
	}

	public String getClient_id() {
		return client_id;
	}

	public String getUsername() {
		return username;
	}

	public String getScope() {
		return scope;
	}

	public String getSub() {
		return sub;
	}

	public String getAud() {
		return aud;
	}

	public String getIss() {
		return iss;
	}

	public String getIat() {
		return iat;
	}

	public String getExp() {
		return exp;
	}

	public abstract boolean isValid();

	public abstract String getId();

}
