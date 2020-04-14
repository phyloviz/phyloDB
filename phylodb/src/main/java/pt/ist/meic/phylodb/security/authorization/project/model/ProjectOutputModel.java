package pt.ist.meic.phylodb.security.authorization.project.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.User;

public class ProjectOutputModel extends SingleOutputModel {

	private final String name;
	private final String description;
	private final User.PrimaryKey[] users;

	public ProjectOutputModel(Project project) {
		super(project.getPrimaryKey().toString(), project.getVersion(), project.isDeprecated());
		this.name = project.getName();
		this.description = project.getDescription();
		this.users = project.getUsers();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public User.PrimaryKey[] getUsers() {
		return users;
	}

}
