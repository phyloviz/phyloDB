package pt.ist.meic.phylodb.security.authentication.user.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.security.authorization.Role;

import java.util.Optional;

public class UserInputModel implements InputModel<User> {

	private final String email;
	private final String provider;
	private final String role;

	public UserInputModel(String email, String provider, String role) {
		this.email = email;
		this.provider = provider;
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public String getProvider() {
		return provider;
	}

	public String getRole() {
		return role;
	}

	@Override
	public Optional<User> toDomainEntity(String... params) {
		return !params[0].equals(email) || provider == null || !Role.exists(role) ? Optional.empty() :
				Optional.of(new User(email, provider, Role.valueOf(role)));
	}

}
