package pt.ist.meic.phylodb.security.project.model;

import pt.ist.meic.phylodb.io.input.InputModel;
import pt.ist.meic.phylodb.security.authorization.Visibility;
import pt.ist.meic.phylodb.security.user.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * A ProjectInputModel is the input model for a project
 * <p>
 * A ProjectInputModel is constituted by the {@link #id} field to identify the project, the {@link #name},
 * that is the project name, the {@link #visibility} which is the project visibility, either private or public,
 * the {@link #description}, that is the project description and the {@link #users}, which are the users that have access to the project.
 */
public class ProjectInputModel implements InputModel<Project> {

	private String id;
	private String name;
	private String visibility;
	private String description;
	private User.PrimaryKey[] users;

	public ProjectInputModel() {
	}

	public ProjectInputModel(String id, String name, String visibility, String description, User.PrimaryKey[] users) {
		this.id = id;
		this.name = name;
		this.visibility = visibility;
		this.description = description;
		this.users = users;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getDescription() {
		return description;
	}

	public User.PrimaryKey[] getUsers() {
		return users;
	}

	@Override
	public Optional<Project> toDomainEntity(String... params) {
		String id = params.length == 0 ? UUID.randomUUID().toString() : params[0];
		return (params.length != 0 && !params[0].equals(this.id)) || name == null || users == null ||
				visibility == null || !Visibility.exists(visibility) ?
				Optional.empty() :
				Optional.of(new Project(id, name, Visibility.valueOf(visibility.toUpperCase()), description, users));
	}

}
