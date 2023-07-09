package pt.ist.meic.phylodb.security.authentication.phyloviz;

import pt.ist.meic.phylodb.security.authentication.TokenInfo;

import java.util.Arrays;

public class PHYLOViZToken extends TokenInfo {

	private static final String SCOPE = "email";

	private String email;

	public PHYLOViZToken() {
	}

	public String getEmail() {
		return email;
	}

	@Override
	public boolean isValid() {
		return this.active && Arrays.asList(this.scope.split(" ")).contains(SCOPE) && this.email != null;
	}

	@Override
	public String getId() {
		return email;
	}

}
