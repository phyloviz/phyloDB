package pt.ist.meic.phylodb.security.authentication.google;

import pt.ist.meic.phylodb.security.authentication.TokenInfo;

import java.util.Arrays;

public class GoogleToken extends TokenInfo {

	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";

	private String email;

	public GoogleToken() {
	}

	public String getEmail() {
		return email;
	}

	@Override
	public boolean isValid() {
		return Arrays.asList(this.scope.split(" ")).contains(SCOPE) && this.email != null;
	}

	@Override
	public String getId() {
		return email;
	}

}
