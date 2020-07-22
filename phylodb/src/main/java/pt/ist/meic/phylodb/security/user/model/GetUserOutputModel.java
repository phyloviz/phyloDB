package pt.ist.meic.phylodb.security.user.model;

import java.util.Objects;

/**
 * A GetUserOutputModel is the output model representation of a {@link User}
 * <p>
 * A GetUserOutputModel is constituted by the {@link #email} and {@link #provider} fields to identify the project, the {@link #deprecated}, and {@link #version} fields which
 * indicates if the user is deprecated, and what version it has. It also contains the {@link #role}, that is the user role.
 */
public class GetUserOutputModel extends UserOutputModel {

	private String role;

	public GetUserOutputModel() {
	}

	public GetUserOutputModel(User user) {
		super(user);
		this.role = user.getRole().getName();
	}

	public String getProvider() {
		return provider;
	}

	public String getRole() {
		return role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GetUserOutputModel that = (GetUserOutputModel) o;
		return super.equals(that) &&
				Objects.equals(role, that.role);
	}

}
