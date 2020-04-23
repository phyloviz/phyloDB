package pt.ist.meic.phylodb.security.authentication.user.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.security.authorization.Role;

import java.util.Optional;

public class UserInputModel implements InputModel<User> {

	private String id;
	private String provider;
	private String role;

	public UserInputModel() {
	}

	public UserInputModel(String email, String provider, String role) {
		this.id = email;
		this.provider = provider;
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public String getProvider() {
		return provider;
	}

	public String getRole() {
		return role;
	}

	@Override
	public Optional<User> toDomainEntity(String... params) {
		return !params[0].equals(id) || !params[1].equals(provider) || !Role.exists(role) ? Optional.empty() :
				Optional.of(new User(id, provider, Role.valueOf(role.toUpperCase())));
	}

}
