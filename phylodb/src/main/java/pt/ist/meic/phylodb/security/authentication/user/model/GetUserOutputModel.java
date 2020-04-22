package pt.ist.meic.phylodb.security.authentication.user.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

import java.util.Objects;

public class UserOutputModel extends SingleOutputModel<User.PrimaryKey> {

	private String provider;
	private String role;

	public UserOutputModel() { }

	public UserOutputModel(User user) {
		super(user);
		this.provider = user.getPrimaryKey().getProvider();
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
		UserOutputModel that = (UserOutputModel) o;
		return  Objects.equals(id, that.id) &&
				Objects.equals(version, that.version) &&
				Objects.equals(deprecated, that.deprecated) &&
				Objects.equals(provider, that.provider) &&
				Objects.equals(role, that.role);
	}

}
