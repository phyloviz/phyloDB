package pt.ist.meic.phylodb.security.authentication.user.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class UserOutputModel extends SingleOutputModel {

	private final String provider;
	private final String role;

	public UserOutputModel(User user) {
		super(user.getPrimaryKey().getId(), user.getVersion(), user.isDeprecated());
		this.provider = user.getPrimaryKey().getProvider();
		this.role = user.getRole().getName();
	}

	public String getProvider() {
		return provider;
	}

	public String getRole() {
		return role;
	}

}
