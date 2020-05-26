package pt.ist.meic.phylodb.security.authorization.project.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.security.authentication.user.model.User;

import java.util.Optional;
import java.util.UUID;

public class ProjectInputModel implements InputModel<Project> {

	private String id;
	private String name;
	private String type;
	private String description;
	private User.PrimaryKey[] users;

	public ProjectInputModel() {
	}

	public ProjectInputModel(String id, String name, String type, String description, User.PrimaryKey[] users) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
		this.users = users;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public User.PrimaryKey[] getUsers() {
		return users;
	}

	@Override
	public Optional<Project> toDomainEntity(String... params) {
		String id = params.length == 0 ? UUID.randomUUID().toString() :params[0];
		return (params.length != 0 && !params[0].equals(this.id)) || name == null || users == null ||
				type == null || (!type.equals("public") && !type.equals("private")) ?
				Optional.empty() :
				Optional.of(new Project(id, name, type, description, users));
	}

}
