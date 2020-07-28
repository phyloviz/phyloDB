package pt.ist.meic.phylodb.security.user.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.security.authorization.Role;

import java.util.Optional;

/**
 * A UserInputModel is the input model for a user
 * <p>
 * A UserInputModel is constituted by the {@link #email} and {@link #provider} fields to identify the user,
 * and the {@link #role}, which is the role of the user.
 */
public class UserInputModel implements InputModel<User> {

	private String email;
	private String provider;
	private String role;

	public UserInputModel() {
	}

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
		return !params[0].equals(email) || !params[1].equals(provider) || !Role.exists(role) ? Optional.empty() :
				Optional.of(new User(email, provider, Role.valueOf(role.toUpperCase())));
	}

}
