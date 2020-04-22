package pt.ist.meic.phylodb.security.authorization.project.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.User;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ProjectOutputModel extends SingleOutputModel<UUID> {

	private String name;
	private String description;
	private User.PrimaryKey[] users;

	public ProjectOutputModel() {
	}

	public ProjectOutputModel(Project project) {
		super(project);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ProjectOutputModel that = (ProjectOutputModel) o;
		return super.equals(that) &&
				Objects.equals(name, that.name) &&
				Objects.equals(description, that.description) &&
				Arrays.equals(users, that.users);
	}

}
